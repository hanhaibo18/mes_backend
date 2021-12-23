package com.richfit.mes.gateway.web.filter;

import com.richfit.mes.gateway.web.constants.GatewayConstant;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http2.HttpConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.symmetric.AES;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sun.security.util.SecurityConstants;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

/**
 * @author sun
 * @Description 密码解密
 */
@Slf4j
@Component
public class PasswordDecoderFilter extends AbstractGatewayFilterFactory {

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    @Value("${security.encode.key:1234567812345678}")
    private String encodeKey;

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String PASSWORD = "password";

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 不是登录请求，直接向下执行
            if (!StringUtils.containsIgnoreCase(request.getURI().getPath(), GatewayConstant.OAUTH_TOKEN_URL)) {
                return chain.filter(exchange);
            }
            // 刷新token，直接向下执行
            String grantType = request.getQueryParams().getFirst("grant_type");
            if (StringUtils.equals(GatewayConstant.REFRESH_TOKEN, grantType)) {
                return chain.filter(exchange);
            }

            Class inClass = String.class;
            Class outClass = String.class;
            ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);

            // 解密生成新的报文
            Mono<?> modifiedBody = serverRequest.bodyToMono(inClass).flatMap(decryptAES());

            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, outClass);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            headers.remove(HttpHeaders.CONTENT_LENGTH);

            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                ServerHttpRequest decorator = decorate(exchange, headers, outputMessage);
                return chain.filter(exchange.mutate().request(decorator).build());
            }));
        };
    }
    private static String decryptAES(String data, String pass)throws Exception  {
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(pass.getBytes(), KEY_ALGORITHM), new IvParameterSpec(pass.getBytes()));
        byte[] result = cipher.doFinal(Base64.decode(data.getBytes(StandardCharsets.UTF_8)));
        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * 原文解密
     * @return
     */
    private Function decryptAES() {
        return s -> {
            // 构建前端对应解密AES 因子
            // TODO
            log.info("{}",s);
            return s;
        };
    }

    /**
     * 报文转换
     * @return
     */
    private ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers,
                                                CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                }
                else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }
}
