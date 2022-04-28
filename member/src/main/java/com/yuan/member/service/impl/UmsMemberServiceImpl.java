package com.yuan.member.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuan.mallapi.domain.member.User;
import com.yuan.mallapi.service.CouponService;
import com.yuan.mallapi.service.UmsMemberService;
import com.yuan.member.entity.UmsMember;
import com.yuan.member.mapper.UmsMemberMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


/**
 * @author 6
 * @description 针对表【ums_member(会员)】的数据库操作Service实现
 * @createDate 2022-03-24 00:28:59
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @DubboReference
    private CouponService couponService;

    @Override
    public String consumerService() {
        return couponService.couponservice();
    }

}
