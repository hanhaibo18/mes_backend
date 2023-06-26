package com.richfit.plm.common;

/**
 * @author sun
 * @Description 枚举常用API操作码
 */
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    INVALID_ARGUMENTS(400, "参数检验失败"),
    ITEM_NOT_FOUND(404, "未找到"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限");
    private long code;
    private String message;
    private Object dubug;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    private ResultCode(long code, String message, Object dubug) {
        this.code = code;
        this.message = message;
        this.dubug = dubug;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
