package com.yuan.member.entity;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import com.yuan.common.utils.SnowFlakeUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员
 *
 * @TableName ums_member
 */
@TableName(value = "ums_member")
@Data
public class UmsMember implements Serializable {
    /**
     * id(type = IdType.AUTO)
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id = SnowFlakeUtil.get().createId();

    /**
     * 会员等级id
     */
    private Long levelId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String header;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 生日
     */
    private Date birth;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 职业
     */
    private String job;

    /**
     * 个性签名
     */
    private String sign;

    /**
     * 用户来源
     */
    private Integer sourceType;

    /**
     * 积分
     */
    private Integer integration;

    /**
     * 成长值
     */
    private Integer growth;

    /**
     * 启用状态
     */
    private Integer status;

    /**
     * 注册时间
     */
    private Date createTime;
    /**
     * 分到哪个数据库
     */
    private Integer sharding_db = RandomUtil.randomInt(0, 2);

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}