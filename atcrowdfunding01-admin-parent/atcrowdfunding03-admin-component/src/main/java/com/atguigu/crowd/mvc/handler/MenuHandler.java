package com.atguigu.crowd.mvc.handler;

import com.atguigu.crowd.entity.Menu;
import com.atguigu.crowd.service.api.MenuService;
import com.atguigu.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller
//@ResponseBody
@RestController
public class MenuHandler {

    @Autowired
    private MenuService menuService;

    @RequestMapping("/menu/get/whole/tree.json")
    /*@ResponseBody*/
    public ResultEntity<Menu> getWholeTreeNew() {
        // 1、查询全部的 Menu 对象
        List<Menu> menuList = menuService.getAll();
        ;
        // 2、声明一个变量用力啊存储找到的节点
        Menu root = null;
        // 3.创建Map对象用来存储id和Menu对象的对应关系便于查找父节点
        Map<Integer, Menu> menuMap = new HashMap<>();
        // 4.遍历menuList填充menuMap
        for (Menu menu : menuList) {
            Integer id = menu.getId();
            menuMap.put(id, menu);
        }
        // 5.再次遍历menuList查找根节点、组装父子节点
        for (Menu menu : menuList) {
            // 6.获取当前menu对象的pid属性值
            Integer pid = menu.getPid();
            // 7.如果pid为null，判定为根节点
            if (pid == null) {
                root = menu;
                continue;
            }
            // 8、pid 不为 null，说明当前节点有父节点，根据 pid 到 menuMap 中查找对应对象
            Menu father = menuMap.get(pid);
            // 9、将当前节点存入父节点的 children 集合
            father.getChildren().add(menu);
        }
        // 10、经过上面运算，根节点包含了整个树形结构，返回根节点就是返回整个树
        return ResultEntity.successWithData(root);
    }
    /*public ResultEntity<Menu> getWholeTreeOld() {
        // 1、查询全部的 Menu 对象
        List<Menu> menuList = menuService.getAll();;
        // 2、声明一个变量用力啊存储找到的节点
        Menu root = null;
        // 3、遍历 menuList
        for (Menu menu : menuList) {
            // 4、获取当前 menu 对象的 pid 属性
            Integer pid = menu.getPid();
            // 5、检查 pid 是否为空
            if (pid == null) {
                // 6、若为空则表示其是父节点，把当前正在遍历的这个 menu 对象赋值
                root = menu;
                // 7、停止本次循环，执行下一次循环
                continue;
            }
            // 8、如果 pid 不为 null，说明当前节点有父节点，找到父节点建立父子关系
            for (Menu maybeFather: menuList) {
                // 9、 获取 maybeFather 的 id 属性
                Integer id = maybeFather.getId();
                // 10、将子节点的 pid 与 疑似父节点的 id 进行比较
                if (Objects.equals(pid, id)) {
                    // 11、将子节点放入父节点的 children 集合
                    maybeFather.getChildren().add(menu);
                    // 12、找到即可停止运行循环，对于子节点来说父节点就一个
                    continue;
                }
            }
        }
        // 13、将组装好的树形结构（根节点对象）返回给浏览器
        return ResultEntity.successWithData(root);
    }*/

    @RequestMapping("/menu/save.json")
    /*@ResponseBody*/
    public ResultEntity<Menu> saveMenu(Menu menu) {
        menuService.saveMenu(menu);
        return ResultEntity.successWithOutData();
    }

    @RequestMapping("/menu/update.json")
    /*@ResponseBody*/
    public ResultEntity<String> updateMenu(Menu menu) {
        menuService.updateMenu(menu);
        return ResultEntity.successWithOutData();
    }

    @RequestMapping("/menu/remove.json")
    /*@ResponseBody*/
    public ResultEntity<String> removeMenu(@RequestParam("id") Integer id) {
        menuService.removeMenu(id);
        return ResultEntity.successWithOutData();
    }
}
