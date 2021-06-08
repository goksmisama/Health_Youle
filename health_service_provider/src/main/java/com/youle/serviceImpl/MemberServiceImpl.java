package com.youle.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.youle.dao.MemberDao;
import com.youle.pojo.Member;
import com.youle.service.MemberService;
import com.youle.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = MemberService.class)
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public class MemberServiceImpl implements MemberService{

    @Autowired
    private MemberDao memberDao;

    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
        if (member.getPassword() !=null){//不是手机快速登录的用户也就是输入了密码的就密码加密注册
            member.setPassword(MD5Utils.md5(member.getPassword()));
        }
        memberDao.add(member);
    }

    @Override
    public List<Integer> findMemberCountByMonth(List<String> month) {
        List<Integer>list = new ArrayList<>();
        for (String s : month) {
            s = s+".31"; //格式2021.05.31
            Integer count = memberDao.findMemberCountBeforeDate(s);
            list.add(count);
        }
        return list;
    }
}
