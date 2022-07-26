package com.atguigu.crowd.service.impl;

import com.atguigu.crowd.entity.Role;
import com.atguigu.crowd.entity.RoleExample;
import com.atguigu.crowd.mapper.AdminMapper;
import com.atguigu.crowd.mapper.RoleMapper;
import com.atguigu.crowd.service.api.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public PageInfo<Role> getPageInfo(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<Role> roleList = roleMapper.selectRoleByKeyword(keyword);
        PageInfo<Role> pageInfo = new PageInfo<>(roleList);
        return pageInfo;
    }

    @Override
    public void saveRole(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void updateRole(Role role) {
        roleMapper.updateByPrimaryKey(role);
    }

    @Override
    public void removeRole(List<Integer> roleIdList) {
        RoleExample example = new RoleExample();
        RoleExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(roleIdList);
        roleMapper.deleteByExample(example);
    }

    @Override
    public List<Role> getAssignedRole(Integer adminId) {
        return roleMapper.selectAssignedRole(adminId);
    }

    @Override
    public List<Role> getUnAssignedRole(Integer adminId) {
        return roleMapper.selectUnAssignedRole(adminId);
    }

    @Override
    public void saveAdminRoleRelationship(Integer adminId, List<Integer> roleIdList) {
        /*// 为了简化操作：先根据 adminId 删除旧的数据，再根据 roleIdList 保存全部新的数据
        // 1.根据 adminId 删除旧的关联关系数据
        adminMapper.deleteOldRelationship(adminId);
        // 2.根据 roleIdList 和 adminId 保存新的关联关系
        if (roleIdList != null && roleIdList.size() > 0) {
            adminMapper.insertNewRelationship(adminId, roleIdList);
        }*/
        // 1、根据 adminId 查询出老的关联关系
        List<Integer> oldRoleIdList = adminMapper.selectOldRealationship(adminId);
        // 2、找出新老集合中相同部分
        // ①声明一个 sameRoleIdList 集合用于存放两个集合中相同的部分
        List<Integer> sameRoleIdList = new ArrayList<>();
        // ②如果新增集合为null，说明只删除角色；若不为null则说明有新增数据
        if (roleIdList != null) {
            // ③遍历老集合中的每一个元素，如果新集合中有该元素，则将其放入 sameRoleIdList 集合中
            for (Integer oldRole : oldRoleIdList) {
                if (roleIdList.contains(oldRole)){
                    sameRoleIdList.add(oldRole);
                }
            }
            // 3、找出老集合中要删除的数据
            for (Integer roleId : sameRoleIdList) {
                if (oldRoleIdList.contains(roleId)) {
                    oldRoleIdList.remove(roleId);
                }
            }
            // 4、找出新集合要新增的数据
            for (Integer roleId : sameRoleIdList){
                if (roleIdList.contains(roleId)) {
                    roleIdList.remove(roleId);
                }
            }
        }
        // 5、如果有要删除的旧数据则执行
        if (oldRoleIdList.size() != 0){
            adminMapper.deleteOldRelationship2(adminId, oldRoleIdList);
        }
        // 6、新增要添加的数据
        if (roleIdList != null && roleIdList.size() > 0) {
            adminMapper.insertNewRelationship(adminId, roleIdList);
        }
    }
}
