package com.yuan.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.member.entity.Role;
import com.yuan.member.service.RoleService;
import com.yuan.member.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author 6
* @description 针对表【sys_role】的数据库操作Service实现
* @createDate 2022-04-02 17:35:53
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




