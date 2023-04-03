package com.kld.mes.wms.utils.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;



/**
 * @author wangchenyu
 * @version 1.0
 * @date 2018/8/31 16:50
 * @implNote http请求工具类
 */
public class HttpClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    private HttpClientUtil(){}
    /**
     * Json格式的请求
     * @param url  请求的路径
     * @param requestMethod 请求方法
     * @param params  请求参数
     * @param charset 参数和返回字符串编码
     * @return 接口响应String
     */
    public static String sendRequest(String url, String requestMethod, String params,String charset) {
        StringBuilder buffer = new StringBuilder();
        String result;
        try {
            URL requestURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("sysid", "c350f36d4e5acd3353a50f3f9b312285");
            connection.setRequestProperty("Cookie","as_ticket=7f4bcf02-2ddd-4dca-911a-162ad3e7f580");
            connection.setRequestProperty("sign-server-auth","c350f36d4e5acd3353a50f3f9b312285|1659060728896|CDBA6BE8B39B1777B683E0B0FA0FDD9C");
            connection.setRequestMethod(requestMethod);
            int flag = 0;
            if (params != null) {
                // 如果不是esb的请求，，是新审批接口，去掉外一层封装的httpData,因为新的审批接口不走esb
                if (params.contains("httpData") && !url.contains("esb")) {
                    params = params.replace("{\"httpData\":", "");
                    params = params.replace("}}", "}");
                    flag = 1;
                }
                OutputStream out = connection.getOutputStream();
                out.write(params.getBytes(charset));
                out.close();
            }
            if (connection.getResponseCode() == StatusCode.SUCCESS) {
                InputStream input = connection.getInputStream();
                InputStreamReader inputReader = new InputStreamReader(input,charset);
                BufferedReader reader = new BufferedReader(inputReader);
                String line;
                while((line = reader.readLine()) != null) {
                       buffer.append(line);
                }
                reader.close();
                inputReader.close();
                input.close();
                connection.disconnect();
                result = buffer.toString();
                // 如果不是esb的请求，是新审批接口，在外一层封装status,因为新的审批接口没有封装
                if (flag == 1) {
                    result = "{\"status\": 200,\"err\": {},\"result\":" + result + "}";
                }
                return result;
            }
        } catch (Exception e) {
           LOGGER.error("HTTP请求工具类出错:{}",e.toString());
        }
        return null;
    }
}
