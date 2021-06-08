package com.youle.dao;

import com.github.pagehelper.Page;
import com.youle.pojo.CheckGroup;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CheckGroupDao {

    Page<CheckGroup> findPage(String queryString);

    void add(CheckGroup checkGroup);

    void addCheckgroupAndCheckItem(Map<String, Integer> map);

    CheckGroup findById(Integer id);

    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    void update(CheckGroup checkGroup);

    void deleteCheckgroupAndCheckItem(Integer id);

    void deleteById(Integer id);

    List<CheckGroup> findAll();

    List<CheckGroup> findCheckGroupBySetmealId(Integer SetmealId);
}
