package com.youle.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCrypyPasswordEncoderUtils {

    static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        String password = "123";
        password = bCryptPasswordEncoder.encode(password);
        System.out.println(password);
    }
}
