package com.youle.dao;

import com.github.pagehelper.Page;
import com.youle.pojo.CheckItem;

public interface MapDao {
    Page<CheckItem> findPage(String queryString);

    void deleteById(Integer id);
}
