package com.youle.dao;

import com.github.pagehelper.Page;
import com.youle.pojo.CheckItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckItemDao {
    public void add(CheckItem checkItem);

    Page<CheckItem> findPage(String queryString);

    void deleteById(Integer id);

    long findCountByCheckItemId(Integer id);

    CheckItem findById(Integer id);

    void update(CheckItem checkItem);

    void deleteByIds(List list);

    List<CheckItem> findAll();

   List<CheckItem> findCheckItemByCheckGroupId(Integer CheckgroupId);
}
