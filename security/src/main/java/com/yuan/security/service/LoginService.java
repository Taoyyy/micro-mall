package com.yuan.security.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.yuan.common.api.CommonResult;

import java.util.Map;

public interface LoginService {
    CommonResult<Map> login(String name, String password);

    CommonResult<String> logout();

    CommonResult<String> register(String name, String password);
}
