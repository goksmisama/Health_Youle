package com.youle.service;

import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;
import com.youle.pojo.Permission;

import java.util.List;

public interface PermissionService {
    PageResult findPage(QueryPageBean queryPageBean);

    void add(Permission permission);

    void edit(Permission permission);

    Permission findById(Integer id);

    List<Permission> findAll();

    void delete(Integer[] ids);
}
