package com.atguigu.crowd.test;

import com.atguigu.crowd.entity.Admin;
import com.atguigu.crowd.entity.Role;
import com.atguigu.crowd.mapper.AdminMapper;
import com.atguigu.crowd.mapper.RoleMapper;
import com.atguigu.crowd.service.api.AdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//指定 Spring 给 Junit 提供的运行期类
@RunWith(SpringJUnit4ClassRunner.class)
//加载 Spring 配置文件的注解
@ContextConfiguration(locations = {"classpath:spring-persist-mybatis.xml", "classpath:spring-persist-tx.xml"})
public class CrowdSpringTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleMapper roleMapper;

    private Logger logger = LoggerFactory.getLogger(CrowdSpringTest.class);

    @Test
    public void test1() {
        // 1、根据 adminId 查询出老的关联关系
        List<Integer> oldRoleIdList = new ArrayList<>();
        oldRoleIdList.add(1);
        oldRoleIdList.add(2);
        oldRoleIdList.add(3);
        oldRoleIdList.add(4);
        oldRoleIdList.add(5);
        List<Integer> roleIdList = new ArrayList<>();
        roleIdList.add(3);
        roleIdList.add(4);
        roleIdList.add(5);
        roleIdList.add(6);
        roleIdList.add(7);
        logger.info("老数组中的值："+oldRoleIdList);
        logger.info("新数组中的值："+roleIdList);
        // 2、找出新老集合中相同部分
        // ①声明一个 sameRoleIdList 集合用于存放两个集合中相同的部分
        List<Integer> sameRoleIdList = new ArrayList<>();
        // ②遍历老集合中的每一个元素，如果新集合中有该元素，则将其放入 sameRoleIdList 集合中
        for (Integer oldRole : oldRoleIdList) {
            if (roleIdList.contains(oldRole)){
                sameRoleIdList.add(oldRole);
            }
        }
        logger.info("新老数组中相同的部分：" + sameRoleIdList);
        // 3、找出老集合中要删除的数据
        // List<Integer> deleteRoleIdList = new ArrayList<>();
        for (Integer roleId : sameRoleIdList) {//[3, 4, 5]
            if (oldRoleIdList.contains(roleId)) {
                oldRoleIdList.remove(roleId);
            }
        }
        logger.info("找出老集合中要删除的数据：" + oldRoleIdList);
        // 4、找出新集合要新增的数据
        // List<Integer> insertRoleIdList = new ArrayList<>();
        for (Integer roleId : sameRoleIdList){
            if (roleIdList.contains(roleId)) {
                roleIdList.remove(roleId);
            }
        }
        logger.info("新集合要新增的数据：" + roleIdList);
    }

    @Test
    public void testConnection() throws SQLException {
        //1、通过数据源对象获取数据源连接
        Connection connection = dataSource.getConnection();
        //2、打印数据库连接
        System.out.println(connection);
    }

    @Test
    public void testInsertAdmin() {
        Admin admin = new Admin(null, "tom", "123", "汤姆", "tom@qq.com", null);
        int count = adminMapper.insert(admin);
        // 如果在实际开发中，所有想查看数值的地方都使用sysout方式打印，会给项目上线运行带来问题！
        // sysout本质上是一个IO操作，通常IO的操作是比较消耗性能的。如果项目中sysout很多，那么对性能的影响就比较大了。
        // 即使上线前专门花时间删除代码中的sysout，也很可能有遗漏，而且非常麻烦。
        // 而如果使用日志系统，那么通过日志级别就可以批量的控制信息的打印。
        System.out.println("受影响的行数="+count);
    }

    @Test
    public void testLog(){
        //1、获取Logger对象，这里传入的Class对象就是当钱打印日志的类
        Logger logger = LoggerFactory.getLogger(CrowdSpringTest.class);
        //2、根据不同日志级别打印日志
        logger.debug("Hello I am Debug level!!!");
        logger.debug("Hello I am Debug level!!!");
        logger.debug("Hello I am Debug level!!!");
        logger.info("Info level!");
        logger.info("Info level!");
        logger.info("Info level!");
        logger.warn("Warn level!!!");
        logger.warn("Warn level!!!");
        logger.warn("Warn level!!!");
        logger.error("Error level!");
        logger.error("Error level!");
        logger.error("Error level!");
    }

    @Test
    public void testInsertDataForTest(){
        for(int i = 0; i < 238; i++) {
            adminMapper.insert(new Admin(null, "test-LoginAcct"+i, "userPswd"+i, "测试用户昵称"+i, "测试用户email"+i, null));
        }
    }

    @Test
    public void testRoleSave() {
        for(int i = 0; i < 235; i++) {
            roleMapper.insert(new Role(null, "测试角色-test"+i));
        }
    }
}
