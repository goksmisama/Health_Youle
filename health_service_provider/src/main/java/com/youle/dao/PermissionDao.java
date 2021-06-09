package com.youle.dao;

import com.github.pagehelper.Page;
import com.youle.pojo.Permission;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PermissionDao {
    Set<Permission> findByRoleId(Integer roleId);

    Page<Permission> selectByCondition(String s);

    void add(Permission permission);

    void edit(Permission permission);

    Permission findById(Integer id);

    void delete(List ids);

    List findAll();
}
