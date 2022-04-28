package com.yuan.member.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuan.mallapi.domain.member.User;
import com.yuan.mallapi.service.MemberService;
import com.yuan.member.entity.UmsMember;
import com.yuan.member.mapper.PermsMapper;
import com.yuan.member.mapper.UmsMemberMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;



@DubboService
@Service
public class MemberServiceImpl implements MemberService {
    @Resource
    UmsMemberMapper MemberMapper;
    @Resource
    PermsMapper permsMapper;

    @Override
    public User findMemberByUsername(String name) throws InterruptedException {
        User user = new User();
        LambdaQueryWrapper<UmsMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmsMember::getUsername, name);
        UmsMember umsMember = MemberMapper.selectOne(queryWrapper);
        BeanUtils.copyProperties(umsMember, user);
        return user;
    }

    @Override
    public List<String> findPermsByUserId(Long id) {
        return permsMapper.selectPermsByUserId(id);
    }

    @Override
    public String registerMember(String name, String password)  {
            UmsMember member = new UmsMember();
            member.setUsername(name);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodePassword = passwordEncoder.encode(password);
            member.setPassword(encodePassword);
            //查询是否有相同用户名的用户
            User user = new User();
            LambdaQueryWrapper<UmsMember> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UmsMember::getUsername, name);
            UmsMember umsMember = MemberMapper.selectOne(queryWrapper);
            if (umsMember != null) {
                System.out.println("用户名已存在");
                System.out.println("null");
                return "null";
            }
            MemberMapper.insertSelective(member);
            System.out.println(name);
            return name;
    }


}
