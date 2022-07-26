package com.atguigu.crowd.handler;

import com.atguigu.crowd.api.MySQLRemoteService;
import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.entity.vo.AddressVO;
import com.atguigu.crowd.entity.vo.MemberLoginVO;
import com.atguigu.crowd.entity.vo.OrderProjectVO;
import com.atguigu.crowd.util.ResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class OrderHandler {

    private Logger logger = LoggerFactory.getLogger(OrderHandler.class);
    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/confirm/return/info/{projectId}/{returnId}")
    public String showPeturnConfirmInfo(@PathVariable("projectId") Integer projectId, @PathVariable("returnId") Integer returnId, HttpSession session) {
        ResultEntity<OrderProjectVO> resultEntity = mySQLRemoteService. getOrderProjectVORemote(projectId, returnId);
        if(ResultEntity.SUCCESS.equals(resultEntity.getResult())) {
            OrderProjectVO orderProjectVO = resultEntity.getData();
            // 为了能够在后续操作中保持orderProjectVO数据，存入Session域
            session.setAttribute("orderProjectVO", orderProjectVO);
        }
        return "confirm_return";
    }

    @RequestMapping("/confirm/order/{returnCount}")
    public String showConfirmOrderInfo(@PathVariable("returnCount") Integer returnCount, HttpSession session) {
        // 1.把接收到的回报数量合并到Session域
        OrderProjectVO orderProjectVO = (OrderProjectVO) session.getAttribute("orderProjectVO");
        orderProjectVO.setReturnCount(returnCount);
        session.setAttribute("orderProjectVO", orderProjectVO);
        // 2.获取当前已登录用户的id
        MemberLoginVO memberLoginVO = (MemberLoginVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
        Integer memberId = memberLoginVO.getId();
        // 3.查询目前的收货地址数据
        ResultEntity<List<AddressVO>> resultEntity = mySQLRemoteService.getAddressVORemote(memberId);
        if(ResultEntity.SUCCESS.equals(resultEntity.getResult())) {
            List<AddressVO> list = resultEntity.getData();
            session.setAttribute("addressVOList", list);
        }
        return "confirm_order";
    }

    @RequestMapping("/save/address")
    public String saveAddress(AddressVO addressVO, HttpSession session) {
        // 1.执行地址信息的保存
        ResultEntity<String> resultEntity = mySQLRemoteService.saveAddressRemote(addressVO);
        logger.debug("地址保存处理结果：" + resultEntity.getResult());
        // 2.从 session 域获取 orderProjectVO 对象
        OrderProjectVO orderProjectVO = (OrderProjectVO) session.getAttribute("orderProjectVO");
        // 3.从 orderProjectVO 对象中获取 returnCount
        Integer returnCount = orderProjectVO.getReturnCount();
        // 4.重定向到指定地址，重新进入确认订单页面
        return "redirect:http://localhost:80/order/confirm/order/"+returnCount;
    }
}
