package com.youle.service;

import com.youle.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    void add(List<OrderSetting> orderSettingList);

    List<Map> getOrderSettingByMonth(String date);

    void updateNumberByDate(OrderSetting orderSetting);
}
