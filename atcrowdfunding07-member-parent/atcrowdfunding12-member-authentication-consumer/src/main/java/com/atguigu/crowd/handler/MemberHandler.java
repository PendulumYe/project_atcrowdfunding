package com.atguigu.crowd.handler;

import com.atguigu.crowd.api.MySQLRemoteService;
import com.atguigu.crowd.api.RedisRemoteService;
import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.entity.po.MemberPO;
import com.atguigu.crowd.entity.vo.MemberLoginVO;
import com.atguigu.crowd.entity.vo.MemberVO;
import com.atguigu.crowd.util.ResultEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Controller
public class MemberHandler {

//    @Autowired
//    private ShortMessageProperties shortMessageProperties;
    @Autowired
    private RedisRemoteService redisRemoteService;
    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    /**
     * 用于接受验证码的方法
     * 注：暂时用随机生成验证码代替
     * @param phoneNum  获取验证码的手机号
     * @return 验证码
     */
    @ResponseBody
    @RequestMapping("/auth/member/send/short/message.json")
    public ResultEntity<String> sendMessage(@RequestParam("phoneNum") String phoneNum) {
        // 生成4位随机数
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 4; i++){
            int index = random.nextInt(10);
            code += index;
        }
        System.out.println("生成验证码：" + code);

        String key = CrowdConstant.REDIS_CODE_PREFIX + phoneNum;
        ResultEntity<String> resultEntity = redisRemoteService.setRedisKeyValueRemoteWithTimeout(key, code, 5, TimeUnit.MINUTES);
        if (ResultEntity.SUCCESS.equals(resultEntity.getResult())) {
            return ResultEntity.successWithData(code);
        }
        return ResultEntity.failed("验证码生成失败");
    }

//    public ResultEntity<String> sendMessage(@RequestParam("phoneNum") String phoneNum) {
//        // 1.发送验证码到 phoneNum 手机
//        ResultEntity<String> sendMessageResultEntity = CrowdUtil.sendCodeByShortMessage(
//                shortMessageProperties.getHost(),
//                shortMessageProperties.getPath(),
//                shortMessageProperties.getMethod(), phoneNum,
//                shortMessageProperties.getAppCode(),
//                shortMessageProperties.getSign(),
//                shortMessageProperties.getSkin());
//
//        // 2.判断短信发送结果
//        if(ResultEntity.SUCCESS.equals(sendMessageResultEntity.getResult())) {
//
//            // 3.如果发送成功，则将验证码存入Redis
//            // ①从上一步操作的结果中获取随机生成的验证码
//            String code = sendMessageResultEntity.getData();
//            // ②拼接一个用于在Redis中存储数据的key
//            String key = CrowdConstant.REDIS_CODE_PREFIX + phoneNum;
//            // ③调用远程接口存入Redis
//            ResultEntity<String> saveCodeResultEntity = redisRemoteService.setRedisKeyValueRemoteWithTimeout(key, code, 15, TimeUnit.MINUTES);
//            // ④判断结果
//            if(ResultEntity.SUCCESS.equals(saveCodeResultEntity.getResult())) {
//                return ResultEntity.successWithOutData();
//            }else {
//                return saveCodeResultEntity;
//            }
//        } else {
//            return sendMessageResultEntity;
//        }
//    }


    /**
     * 注册会员账号
     * @param memberVO  页面提交的表单封装成的 VO 对象
     * @param modelMap
     * @return 若失败，则重新进入注册页；若成功重定向至主页
     */
    @RequestMapping("/auth/do/member/register")
    public String register(MemberVO memberVO, ModelMap modelMap){
        // 1.获取用户输入的手机号
        String phoneNum = memberVO.getPhoneNum();

        // 2.拼Redis中存储验证码的Key
        String key = CrowdConstant.REDIS_CODE_PREFIX + phoneNum;

        // 3.从Redis读取Key对应的value
        ResultEntity<String> resultEntity = redisRemoteService.getRedisStringValueByKeyRemote(key);

        // 4.检查查询操作是否有效
        String result = resultEntity.getResult();
        if(ResultEntity.FAILED.equals(result)) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, resultEntity.getMessage());
            return "member-reg";
        }
        String redisCode = resultEntity.getData();
        if(redisCode == null) {

            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_CODE_NOT_EXISTS);

            return "member-reg";
        }

        // 5.如果从Redis能够查询到value则比较表单验证码和Redis验证码
        String formCode = memberVO.getCode();
        if(!Objects.equals(formCode, redisCode)) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_CODE_INVALID);
            return "member-reg";
        }

        // 6.如果验证码一致，则从Redis删除
        redisRemoteService.removeRedisKeyRemote(key);

        // 7.执行密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String userpswdBeforeEncode = memberVO.getUserpswd();
        String userpswdAfterEncode = passwordEncoder.encode(userpswdBeforeEncode);
        memberVO.setUserpswd(userpswdAfterEncode);

        // 8.执行保存
        // ①创建空的MemberPO对象
        MemberPO memberPO = new MemberPO();
        // ②复制属性
        BeanUtils.copyProperties(memberVO, memberPO);
        // ③调用远程方法
        ResultEntity<String> saveMemberResultEntity = mySQLRemoteService.saveMember(memberPO);
        if(ResultEntity.FAILED.equals(saveMemberResultEntity.getResult())) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, saveMemberResultEntity.getMessage());
            return "member-reg";
        }

        // 使用重定向避免刷新浏览器导致重新执行注册流程
        return "redirect:http://localhost:80/auth/member/to/login/page";
    }

    /**
     * 登录功能
     * @param loginacct 登录账号
     * @param userpswd  登录密码
     * @param modelMap
     * @param session
     * @return  重定向->登录页面访问路径
     */
    @RequestMapping("/auth/member/do/login")
    public String login(@RequestParam("loginacct") String loginacct, @RequestParam("userpswd") String userpswd, ModelMap modelMap, HttpSession session) {
        // 1.调用远程接口根据登录账号查询MemberPO对象
        ResultEntity<MemberPO> resultEntity = mySQLRemoteService.getMemberPOByLoginAcctRemote(loginacct);
        if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, resultEntity.getMessage());
            return "member-login";
        }
        MemberPO memberPO = resultEntity.getData();
        if (memberPO == null){
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        // 2.比较密码
        String userpswdDataBase = memberPO.getUserpswd();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matcheResult = passwordEncoder.matches(userpswd, userpswdDataBase);
        if (!matcheResult){
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        // 3.创建MemberLoginVO对象存入Session域
        MemberLoginVO memberLoginVO = new MemberLoginVO(memberPO.getId(), memberPO.getUsername(), memberPO.getEmail());
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER, memberLoginVO);
        return "redirect:http://localhost:80/auth/member/to/center/page";
    }

    @RequestMapping("/auth/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:http://localhost:80/";
    }
}
