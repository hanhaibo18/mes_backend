package com.bsjx.mes.pdm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

@Configuration
public class PdmConfiguration {

    @Value("${pdm.webservice.uri}")
    private String pdmWebServiceUri;

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.bsjx.mes.pdm.wsdl");
        return marshaller;
    }

    @Bean
    public PdmClient pdmClient(Jaxb2Marshaller marshaller) {
        PdmClient client = new PdmClient();
        client.setDefaultUri(pdmWebServiceUri);
        client.setMarshaller(marshaller);
        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
        messageSender.setConnectionTimeout(600000);
        messageSender.setReadTimeout(600000);
        client.setMessageSender(messageSender);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
