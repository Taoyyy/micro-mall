package com.yuan.security.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import com.yuan.mallapi.service.MemberService;
import com.yuan.security.domain.LoginUser;
import com.yuan.security.service.LoginService;
import com.yuan.common.api.CommonResult;
import com.yuan.common.utils.JwtUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @DubboReference
    private MemberService MemberService;

    @Override

    public CommonResult<Map> login(String name, String password) {

        //AuthenticationManager authenticate进行认证
        // 把用户名和密码交给manager,此Manager会调用UserDetailsService从数据库获取数据来对比认证。
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(name, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }
        //如果认证通过，使用UserId生成jwt，jwt作为result返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String uid = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(uid);
        //把完整的用户信息存入redis，UserId做为key
        String s = JSONObject.toJSONString(loginUser);
        redisTemplate.opsForValue().set("login:" + uid, s, 12, TimeUnit.HOURS);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        return CommonResult.success(map);
    }

    @Override
    public CommonResult<String> logout() {
        //获取SecurityContextHolder中的用户id
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUser().getId();
        //删除redis中的值
        String redisKey = "login:" + userid;
        redisTemplate.delete(redisKey);
        return CommonResult.success("注销成功");
    }

    @Override
    public CommonResult<String> register(String name, String password) {

        String username = MemberService.registerMember(name, password);
        if ("null".equals(username)) {
            return CommonResult.failed("用户名：" + name + "已存在，请重新注册");
        }
        return CommonResult.success("注册成功，" + name);
    }
}
