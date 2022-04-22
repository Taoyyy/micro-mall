package com.yuan.chat.entity.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class MemberVo implements Serializable {
    /**
     * id
     */

    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 服务器地址
     */
    private String url;
}
