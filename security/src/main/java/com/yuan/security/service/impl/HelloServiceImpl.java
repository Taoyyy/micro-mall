package com.yuan.security.service.impl;

import com.yuan.security.domain.LoginUser;
import com.yuan.security.service.HelloService;
import com.yuan.common.api.CommonResult;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public CommonResult<String> hello() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String username = loginUser.getUsername();
        return CommonResult.success("hello," + username);
    }
}
