package com.yuan.mallapi.domain.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName account_tbl
 */
@TableName(value = "account_tbl")
@Data
public class AccountTbl implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String userId;

    /**
     *
     */
    private Integer money;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}