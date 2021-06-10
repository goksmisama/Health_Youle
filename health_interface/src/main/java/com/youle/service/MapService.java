package com.youle.service;

import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;

public interface MapService {
    PageResult findPage(QueryPageBean queryPageBean);

    void deleteById(Integer id);
}
