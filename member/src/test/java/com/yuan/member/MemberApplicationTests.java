package com.yuan.member;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuan.mallapi.domain.member.User;
import com.yuan.member.entity.UmsMember;
import com.yuan.member.mapper.PermsMapper;
import com.yuan.member.mapper.UmsMemberMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MemberApplicationTests {
    @Autowired
    private UmsMemberMapper memberMapper;


    @Test
    void contextLoads() {
    }


    @Test
    public void testUserMapper(){
//        List<UmsMember> umsMembers=memberMapper.selectList(null);
//        LambdaQueryWrapper<UmsMember> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(UmsMember::getUsername, "源啊圆");
//        System.out.println("可以不");
//        UmsMember umsMember=memberMapper.selectOne(queryWrapper);
//        System.out.println(umsMember);


            User user = new User();
            LambdaQueryWrapper<UmsMember> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UmsMember::getUsername, "源啊圆");
            UmsMember umsMember = memberMapper.selectOne(queryWrapper);
            BeanUtils.copyProperties(umsMember, user);
        System.out.println(user);

    }

}
