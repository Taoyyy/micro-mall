package com.yuan.member.controller;


import com.yuan.mallapi.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 会员 前端控制器
 * </p>
 *
 * @author taoyyy
 * @since 2022-03-23
 */
@RestController
@RequestMapping("/member")
public class UmsMemberController {

    @Autowired
    private UmsMemberService umsMemberService;

    @GetMapping("/service")
    public String service() {
        return umsMemberService.consumerService();
    }

}
