package com.yuan.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 枚举了一些常用API操作码

 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    FAIL(501, "秒杀商品失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限");
    private final long code;
    private final String message;
}
