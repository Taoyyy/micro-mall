package com.yuan.mallapi.service;

import com.yuan.mallapi.domain.member.User;

import java.util.List;


public interface MemberService {

    User findMemberByUsername(String name) throws InterruptedException;

    List<String> findPermsByUserId(Long id);

    String registerMember(String name, String password);

}
