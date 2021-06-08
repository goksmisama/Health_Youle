package com.youle.jobs;

/*自定义job,实现定时清理垃圾图片*/

import com.youle.constant.RedisConstant;
import com.youle.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

public class ClearImgJob {

    @Autowired
    private JedisPool jedisPool;


    public void clearImg(){
        //获取redis中 两个set的差值       获取数据源    计算差值的方法
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        if (set !=null){
            for (String picName : set) {
                //删除七牛云上的图片
                QiniuUtils.deleteFileFromQiniu(picName);
                //删除Redis集合中的图片名称
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,picName);
            }
        }
    }
}
