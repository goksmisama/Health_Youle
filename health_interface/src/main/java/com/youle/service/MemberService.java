package com.youle.service;

import com.youle.pojo.Member;

import java.util.List;

public interface MemberService {

    Member findByTelephone(String telephone);

    void add(Member member);

    List<Integer> findMemberCountByMonth(List<String> list);
}
