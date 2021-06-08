package com.youle.dao;

import com.youle.pojo.User;

public interface UserDao {
    User findByUserName(String username);
}
