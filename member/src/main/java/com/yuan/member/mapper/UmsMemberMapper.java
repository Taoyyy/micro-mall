package com.yuan.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.member.entity.UmsMember;


/**
 * @author 6
 * @description 针对表【ums_member(会员)】的数据库操作Mapper
 * @createDate 2022-03-24 00:28:59
 * @Entity com.yuan.mallapi.entity.member.UmsMember
 */


public interface UmsMemberMapper extends BaseMapper<UmsMember> {
    int insertSelective(UmsMember umsMember);


}
