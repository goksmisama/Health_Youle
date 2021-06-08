package com.youle.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.youle.constant.MessageConstant;
import com.youle.constant.RedisMessageConstant;
import com.youle.entity.Result;
import com.youle.pojo.Order;
import com.youle.service.OrderService;
import com.youle.utils.tenxunUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RequestMapping("/order")
@RestController
public class OrderController {

    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;

    public OrderController() {
    }

    @RequestMapping("/submit.do")
    public Result submit(@RequestBody Map map){
        //获取用户输入的手机号  用于验证验证码输入是否正确
        String telephone = (String) map.get("telephone");
        //获取redis中缓存的验证码
        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        //获取用户输入的验证码
        String validateCode = (String) map.get("validateCode");
        //校验验证码是否正确
        if (codeInRedis !=null && validateCode !=null && codeInRedis.equals(validateCode)){
            Result result = null;
            try {
                //调用体检预约服务
                map.put("orderType", Order.ORDERTYPE_WEIXIN);
                result=orderService.order(map);
            } catch (Exception e) {
                e.printStackTrace();
                //预约失败
                return result;
            }
            if (result.isFlag()){
                //预约成功 发送短信通知
                tenxunUtils.successful(telephone);
            }
            return result;

        }else {
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }

    @RequestMapping("/findById.do")
    public Result findById(Integer id){
        try {
            Map map = orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
