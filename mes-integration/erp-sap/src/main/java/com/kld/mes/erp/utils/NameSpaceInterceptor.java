package com.kld.mes.erp.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptorAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 用来在发送的XML消息头中，添加urn:sap-com:document:sap:soap:functions:mc-style这个nameSpace
 * 否则 ERP不能正确解析XML消息
 *
 * @Author: GaoLiang
 * @Date: 2022/7/22 8:41
 */
@Slf4j
public class NameSpaceInterceptor extends ClientInterceptorAdapter {

    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {

        WebServiceMessage request = messageContext.getRequest();

        if (request instanceof SoapMessage) {
            SoapMessage soapMessage = (SoapMessage) request;
            soapMessage.getEnvelope().addNamespaceDeclaration("urn", "urn:sap-com:document:sap:soap:functions:mc-style");

        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            request.writeTo(os);
            log.debug(os.toString("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}
