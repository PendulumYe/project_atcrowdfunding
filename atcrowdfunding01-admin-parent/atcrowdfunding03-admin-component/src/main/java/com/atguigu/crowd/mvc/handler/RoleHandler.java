package com.atguigu.crowd.mvc.handler;

import com.atguigu.crowd.entity.Role;
import com.atguigu.crowd.service.api.RoleService;
import com.atguigu.crowd.util.ResultEntity;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class RoleHandler {

    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasRole('系统最高权限管理员')")
    @RequestMapping("/role/get/page/info.json")
    @ResponseBody
    public ResultEntity<PageInfo<Role>> getPageInfo(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                                    @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        PageInfo<Role> pageInfo = roleService.getPageInfo(pageNum, pageSize, keyword);
        return ResultEntity.successWithData(pageInfo);
    }

    @RequestMapping("/role/save.json")
    @ResponseBody
    public ResultEntity<Role> saveRole(Role role) {
        roleService.saveRole(role);
        return ResultEntity.successWithOutData();
    }

    @RequestMapping("/role/update.json")
    @ResponseBody
    public ResultEntity<Role> updateRole(Role role) {
        roleService.updateRole(role);
        return ResultEntity.successWithOutData();
    }

    @RequestMapping("/role/remove/by/role/id/array.json")
    @ResponseBody
    public ResultEntity<Role> removeByRoleIdArry(@RequestBody List<Integer> roleIdList) {
        roleService.removeRole(roleIdList);
        return ResultEntity.successWithOutData();
    }

}
