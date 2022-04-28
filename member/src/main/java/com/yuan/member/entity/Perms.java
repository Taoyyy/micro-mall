package com.yuan.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * @TableName sys_perms
 */
@TableName(value = "sys_perms")
@Data
public class Perms implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 权限名
     */
    private String permsName;

    /**
     * 0正常，1停用
     */
    private String status;

    /**
     * 路由地址
     */
    private String path;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}