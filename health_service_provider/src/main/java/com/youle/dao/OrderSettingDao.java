package com.youle.dao;

import com.youle.pojo.OrderSetting;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderSettingDao {

    Long findCountByOrderDate(Date orderDate);
    //更新可预约人数
    void UpdateNumberByOrderDate(OrderSetting orderSetting);

    void add(OrderSetting orderSetting);

    //根据日期范围查询预约设置信息
    List<OrderSetting> getOrderSettingByMonth(Map map);

    //更新已预约人数
    public void editReservationsByOrderDate(OrderSetting orderSetting);

    //根据预约日期查询预约设置信息
    public OrderSetting findByOrderDate(Date orderDate);
}
