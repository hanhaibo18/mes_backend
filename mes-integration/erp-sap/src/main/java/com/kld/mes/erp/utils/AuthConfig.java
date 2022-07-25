package com.kld.mes.erp.utils;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.*;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import java.io.IOException;

/**
 * 用来在调用ERP接口时，传入认证信息
 *
 * @Author: GaoLiang
 * @Date: 2022/7/22 10:54
 */
@Component
public class AuthConfig {

    @Value("${interface.erp.username}")
    private String username;

    @Value("${interface.erp.password}")
    private String password;

    public HttpComponentsMessageSender getSender() {

        RequestConfig requestConfig = RequestConfig.custom()
                .setAuthenticationEnabled(true)
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        HttpClient httpClient = httpClientBuilder
                .addInterceptorFirst(new ContentLengthHeaderRemover())
//                .addInterceptorFirst(new PreemptiveAuthInterceptor())
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(credentialsProvider())
                .build();

        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpClient);
        return messageSender;

    }

    private CredentialsProvider credentialsProvider() {

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, credentials());
        return credentialsProvider;

    }

    private Credentials credentials() {

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                username, password);

        return credentials;
    }

    private class ContentLengthHeaderRemover implements HttpRequestInterceptor {

        @Override
        public void process(HttpRequest request, HttpContext context)
                throws HttpException, IOException {

            // fighting org.apache.http.protocol.RequestContent's
            // ProtocolException("Content-Length header already present");
            request.removeHeaders(HTTP.CONTENT_LEN);
        }
    }

    //    暂时没作用
    static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {

        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
            AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

            // If no auth scheme avaialble yet, try to initialize it
            // preemptively
            if (authState.getAuthScheme() == null) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
                    if (creds == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.setAuthScheme(authScheme);
                    authState.setCredentials(creds);
                }
            }

        }

    }

}
