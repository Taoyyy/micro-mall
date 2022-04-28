package com.yuan.member.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.mallapi.domain.member.AccountTbl;

/**
 * @author 6
 * @description 针对表【account_tbl】的数据库操作Mapper
 * @createDate 2022-04-18 19:53:01
 * @Entity com.yuan.member.entity.AccountTbl
 */
public interface AccountTblMapper extends BaseMapper<AccountTbl> {

    void debit(String userId, int money);
}




