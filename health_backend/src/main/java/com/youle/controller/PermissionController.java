package com.youle.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.youle.constant.MessageConstant;
import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;
import com.youle.entity.Result;
import com.youle.pojo.Permission;
import com.youle.service.PermissionService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Reference
    private PermissionService permissionService;
    /*增加
    删除
    查询
    编辑*/
    @RequestMapping("/add.do")
    private Result add(@RequestBody Permission permission){
        try {
            permissionService.add(permission);
            return new Result(true, "ADD_PREMISSION_SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"ADD_PREMISSION_FAIL");
        }
    }

    @RequestMapping("/delete.do")
    private Result delete(@RequestParam(required = true) Integer[] ids){
        try {
            System.out.println(ids);
            permissionService.delete(ids);
            return new Result(true, "DELETE_PREMISSION_SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"DELETE_PREMISSION_FAIL");
        }
    }

    @RequestMapping("/findAll.do")
    private Result findAll(){
        try {
            List<Permission> list = permissionService.findAll();
            return new Result(true, "QUERY_PREMISSION_SUCCESS",list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"QUERY_PREMISSION_FAIL");
        }
    }

    @RequestMapping("/findPage.do")
    private PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = permissionService.findPage(queryPageBean);
        return pageResult;
    }

    @RequestMapping("/findById.do")
    private Result findById(@RequestParam(name = "id",required = true) Integer id){
        try {
            Permission permission = permissionService.findById(id);
            return new Result(true, "QUERY_PREMISSION_SUCCESS",permission);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"QUERY_PREMISSION_FAIL");
        }
    }

    @RequestMapping("/edit.do")
    private Result edit(@RequestBody Permission permission){
        try {
            System.out.println(permission.getId());
            permissionService.edit(permission);
            return new Result(true, "QUERY_PREMISSION_SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"QUERY_PREMISSION_FAIL");
        }
    }

}
