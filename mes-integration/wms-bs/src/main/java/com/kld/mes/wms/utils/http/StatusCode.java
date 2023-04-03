package com.kld.mes.wms.utils.http;

/**
 * @author wangchenyu
 * @date 2018-08-27
 * @implNote 标准的HTTP状态码
 */
public class StatusCode {

    private StatusCode(){}
    /**
     *  (请求成功) 服务器已成功处理了请求
     */
    public static final int SUCCESS = 200;
    /**
     * （服务器内部错误）  服务器遇到错误，无法完成请求。
     */
    public static final int ERROR = 500;
    /**
     * （禁止）参数不合法，服务器拒绝请求。
     */
    public static final int PARAM_ILLEGAL = 403;
    /**
     *（错误网关） 服务作为网关或代理，从上游服务器收到错误或无效响应。
     */
    public static final int BAD_GATEWAY = 502;
    /**
     * （网关超时）服务作为网关或代理，但是没有及时从上游服务器收到请求。
     */
    public static final int TIMEOUT_GATEWAY = 504;

    public static final String SUCCESS_CODE = "200";

    public static final String UNKNOWN_CODE = "-1";
    public static final String UNKNOWN_MSG = "遇到了一点问题，请联系开发人员!";
    private String code;
    private String msg;

    public StatusCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}
