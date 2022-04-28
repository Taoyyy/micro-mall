package com.yuan.member.mapper;

import com.yuan.member.entity.Perms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author 6
 * @description 针对表【sys_perms】的数据库操作Mapper
 * @createDate 2022-04-02 17:50:42
 * @Entity com.yuan.member.entity.Perms
 */
public interface PermsMapper extends BaseMapper<Perms> {
    List<String> selectPermsByUserId(Long userid);
}




