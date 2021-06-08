package com.youle.service;

import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;
import com.youle.pojo.CheckGroup;

import java.util.List;

public interface CheckGroupService {

    PageResult findPage(QueryPageBean queryPageBean);

    void add(Integer[] ids, CheckGroup checkGroup);

    CheckGroup findById(Integer id);

    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    void update(Integer[] ids, CheckGroup checkGroup);

    void delete(Integer id);

    List<CheckGroup> findAll();
}
