package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.entity.User;
import com.wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wms
 * @since 2025-11-12
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<User> list(){
        return userService.list();
    }

    //新增
    @PostMapping("/save")
    public boolean save(@RequestBody User user){
        return  userService.save(user);
    }
    //修改
    @PostMapping("/mod")
    public boolean mod(@RequestBody User user){
        return  userService.updateById(user);
    }
    //新增或修改
    @PostMapping("/saveOrMod")
    public boolean saveOrMod(@RequestBody User user){
        return  userService.saveOrUpdate(user);
    }
    //删除
    @GetMapping("/delete")
    public boolean delete(Integer id){
        return userService.removeById(id);
    }
    //查询（模糊、匹配）注释
    @PostMapping("/listPage")
//    public List<User> listPage(@RequestBody HashMap map){
    public List<User> listPage(@RequestBody QueryPageParam query){
        System.out.println(query);


        System.out.println("num==="+query.getPageNum());
        System.out.println("size==="+query.getPageSize());

        HashMap param =query.getParam();
        String name = (String)param.get("name");
        System.out.println("name==="+(String)param.get("name") );
//        System.out.println("no==="+(String)param.get("no") );
//        LambdaQueryWrapper<User> LambdaQueryWrapper = new LambdaQueryWrapper<>();
//        LambdaQueryWrapper.like(User::getName,user.getName());
//        return  userService.list(LambdaQueryWrapper);
        Page<User> page = new Page<User>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.like(User::getName,name);

        IPage result= userService.page(page,LambdaQueryWrapper);
        System.out.println("total=="+result.getTotal() );


        return result.getRecords();
    }
    @PostMapping("/listPageC")
//    public List<User> listPage(@RequestBody HashMap map){
    public List<User> listPageC(@RequestBody QueryPageParam query){
        System.out.println(query);


        System.out.println("num==="+query.getPageNum());
        System.out.println("size==="+query.getPageSize());

        HashMap param =query.getParam();
        String name = (String)param.get("name");
        System.out.println("name==="+(String)param.get("name") );

        Page<User> page = new Page<User>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.like(User::getName,name);

//        IPage result= userService.pageC(page);
        IPage result= userService.pageCC(page,LambdaQueryWrapper);
        System.out.println("total=="+result.getTotal() );


        return result.getRecords();
    }
}
