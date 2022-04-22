package com.yuan.chat.controller;

import com.yuan.chat.entity.vo.MemberVo;
import com.yuan.chat.service.ChatService;
import com.yuan.common.api.CommonResult;
import com.yuan.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    ChatService chatService;

    //客户端注册一个NettyServer地址
    @GetMapping("/server")
    public CommonResult<MemberVo> getServerAddress(HttpServletRequest request) throws Exception {
        String jwt = request.getHeader("token");
        Claims claims = JwtUtil.parseJWT(jwt);
        String id0 = String.valueOf(claims.get("sub"));
        Long id = Long.parseLong(id0);
        String username = String.valueOf(claims.get("username"));
        MemberVo memberVo = new MemberVo();
        memberVo.setId(id);
        memberVo.setUsername(username);
        String nettyServerAddress = chatService.findNettyServer(id);
        memberVo.setUrl(nettyServerAddress);
        return CommonResult.success(memberVo);
    }

    //TODO 客户端从redis拿到接收者的服务器的地址
}
