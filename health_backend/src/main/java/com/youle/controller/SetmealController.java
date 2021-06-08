package com.youle.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.youle.constant.MessageConstant;
import com.youle.constant.RedisConstant;
import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;
import com.youle.entity.Result;
import com.youle.pojo.Setmeal;
import com.youle.service.SetmealService;
import com.youle.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/setmeal")
@RestController
public class SetmealController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private SetmealService setmealService;

    @RequestMapping("/upload.do")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){
        //获取文件的完整名
        String originalFilename = imgFile.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        //截取文件的后缀
        String suffix = originalFilename.substring(index - 1);
        //使用uuid
        String filename = "桥本" + UUID.randomUUID().toString() + suffix;
        //使用七牛云工具包上传
        try {
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),filename);
            //在redis中存上传图片的名称 用于后续删除垃圾图片
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,filename);
            return new Result(true,MessageConstant.PIC_UPLOAD_SUCCESS,filename);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
    }

    @RequestMapping("/add.do")
    public Result add(@RequestParam(name = "checkgroupIds",required = true) Integer[] ids,@RequestBody Setmeal setmeal){
        try {
            setmealService.add(ids,setmeal);
            return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
    }

    @RequestMapping("/findPage.do")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult=setmealService.findPage(queryPageBean);
        return pageResult;
    }

    @RequestMapping("/findById.do")
    public Result findById(@RequestParam(name = "id",required = true) Integer id){
        try {
            Setmeal setmeal =setmealService.findById(id);
            return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

    @RequestMapping("/findCheckGroupIdBySetmealId.do")
    public Result findCheckGroupIdBySetmealId(@RequestParam(name = "id",required = true) Integer id){
        List<Integer> list;
        try {
             list=setmealService.findCheckGroupIdBySetmealId(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,list);
    }

    @RequestMapping("/update.do")
    public Result update(@RequestParam(name = "checkgroupIds",required = true) Integer[] ids,@RequestBody Setmeal setmeal){
        try {
            setmealService.update(ids,setmeal);
            return new Result(true,MessageConstant.EDIT_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.EDIT_SETMEAL_FAIL);
        }
    }
}
