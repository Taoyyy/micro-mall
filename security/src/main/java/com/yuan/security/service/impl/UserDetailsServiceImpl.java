package com.yuan.security.service.impl;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.yuan.security.domain.LoginUser;

import com.yuan.mallapi.domain.member.User;
import com.yuan.mallapi.service.MemberService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @DubboReference
    private MemberService MemberService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        //远程调用查询用户信息
        User user = null;
        try {
            user = MemberService.findMemberByUsername(username);
        } catch (InterruptedException e) {
            System.out.println("服务器超时");
        }
        //TODO 查询用户权限信息
        //把数据封装成UserDetails返回
        Long id = user.getId();
        List<String> perms = MemberService.findPermsByUserId(id);
        return new LoginUser(user, perms);
    }


}
