package com.kld.mes.erp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

/**
 * 生成调用erpwebservice的template工厂类
 *
 * @Author: GaoLiang
 * @Date: 2022/7/22 14:04
 */
@Component
public class WsTemplateFactory {

    @Autowired
    private AuthConfig authConfig;

    public WebServiceTemplate generateTemplate(String packageName) {

        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

        marshallerConfig(webServiceTemplate, packageName);

        commConfig(webServiceTemplate);

        return webServiceTemplate;
    }


    private void marshallerConfig(WebServiceTemplate webServiceTemplate, String packageName) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(packageName);

        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);

    }

    private void commConfig(WebServiceTemplate webServiceTemplate) {

        webServiceTemplate.setInterceptors(new ClientInterceptor[]{new NameSpaceInterceptor()});
        webServiceTemplate.setMessageSender(authConfig.getSender());

    }

}
