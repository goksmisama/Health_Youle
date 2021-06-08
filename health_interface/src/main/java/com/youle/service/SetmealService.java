package com.youle.service;

import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;
import com.youle.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealService {
    void add(Integer[] ids, Setmeal setmeal);

    PageResult findPage(QueryPageBean queryPageBean);

    Setmeal findById(Integer id);

    List<Integer> findCheckGroupIdBySetmealId(Integer id);

    void update(Integer[] ids, Setmeal setmeal);

    List<Setmeal> findAll();

    List<Map<String, Object>> findSetmealCount();
}
