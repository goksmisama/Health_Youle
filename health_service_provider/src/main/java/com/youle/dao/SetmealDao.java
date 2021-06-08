package com.youle.dao;

import com.github.pagehelper.Page;
import com.youle.pojo.Setmeal;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SetmealDao {
    void add(Setmeal setmeal);

    void addSetmealidAndCheckGroupId(Map<String, Integer> map);

    Page findPage(String queryString);

    Setmeal findById(Integer id);

    List<Integer> findCheckGroupIdBySetmealId(Integer id);

    void update(Setmeal setmeal);

    void deleteSetmealIdAndCheckgroupId(Integer id);

    List<Setmeal> findAll();

    List<Map<String, Object>> findSetmealCount();
}
