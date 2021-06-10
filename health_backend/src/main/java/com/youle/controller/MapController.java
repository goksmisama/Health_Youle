package com.youle.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.youle.constant.MessageConstant;
import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;
import com.youle.entity.Result;
import com.youle.service.MapService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
public class MapController {
    @Reference
    private MapService mapService;

    @RequestMapping("/findPage.do")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult pageResult = mapService.findPage(queryPageBean);
        return pageResult;
    }

    @RequestMapping("/delete.do")
    public Result deleteById(@RequestParam(name = "id", required = true) Integer id) {
        try {
            mapService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_MAP_FAIL);
        }
        return new Result(true, MessageConstant.DELETE_MAP_SUCCESS);
    }
}
