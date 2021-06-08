package com.youle.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.youle.dao.OrderSettingDao;
import com.youle.pojo.OrderSetting;
import com.youle.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderSettingService.class)
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Override
    public void add(List<OrderSetting> list) {
        if (list!=null && list.size()>0){
            for (OrderSetting orderSetting : list) {
                //检查日期是否存在
                Long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if (count>0){
                    //已经存在，执行更新操作
                    orderSettingDao.UpdateNumberByOrderDate(orderSetting);
                }else {
                    //不存在，执行添加操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }

    }

    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        String dateBegin =date+"-1";
        String dateEnd = date+"-31";
        System.out.println(dateBegin);
        System.out.println(dateEnd);
        Map map = new HashMap();
        map.put("dateBegin",dateBegin);
        map.put("dateEnd",dateEnd);
        List<OrderSetting>list=orderSettingDao.getOrderSettingByMonth(map);
        List<Map> data = new ArrayList();
        for (OrderSetting orderSetting : list) {
            Map orderSettingMap = new HashMap();
            orderSettingMap.put("date",orderSetting.getOrderDate().getDate());//因为前端要的不是整个日期而是几号
            orderSettingMap.put("number",orderSetting.getNumber());
            orderSettingMap.put("reservations",orderSetting.getReservations());
            data.add(orderSettingMap);
        }
        return data;
    }

    @Override
    public void updateNumberByDate(OrderSetting orderSetting) {
        //查询日期是否存在
        Long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if (count>0){
            //存在
            orderSettingDao.UpdateNumberByOrderDate(orderSetting);
        }else {
            //不存在  就添加
            orderSettingDao.add(orderSetting);
        }
    }
}
