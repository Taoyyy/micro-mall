package com.yuan.member.service.impl;



import com.yuan.mallapi.service.AccountService;


import com.yuan.member.mapper.AccountTblMapper;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 6
 * @description 针对表【account_tbl】的数据库操作Service实现
 * @createDate 2022-04-18 19:53:01
 */
@DubboService
@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    AccountTblMapper accountMapper;

    @Override
    @Transactional
    public void debit(String userId, int money) {
        accountMapper.debit(userId,money);
    }
}




