package com.yuan.security.controller;

import com.yuan.security.service.HelloService;
import com.yuan.security.service.LoginService;
import com.yuan.common.api.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {
    @Autowired
    LoginService loginService;
    @Autowired
    HelloService helloService;

    @PostMapping("/login")
    public CommonResult<Map> login(@RequestParam("name") String name, @RequestParam("password") String password) {
        try {
            return loginService.login(name, password);
        } catch (Exception e) {
            return CommonResult.failed("服务器开小差，请稍后再试");
        }
    }

    @PostMapping("/logout")
    public CommonResult<String> logout() {
        return loginService.logout();
    }

    @GetMapping("/hello")
    public CommonResult<String> hello() {
        return helloService.hello();
    }

    @GetMapping("/hello2")
    public CommonResult<String> hello2() {
        return helloService.hello();
    }

    @PostMapping("/register")
    public CommonResult<String> register(@RequestParam("name") String name, @RequestParam("password") String password) throws InterruptedException {

        return loginService.register(name, password);
    }
}
