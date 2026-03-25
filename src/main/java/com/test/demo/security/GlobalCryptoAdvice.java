package com.test.demo.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(annotations = Controller.class)
public class GlobalCryptoAdvice implements RequestBodyAdvice, ResponseBodyAdvice<Object> {

    private final PayloadCryptoService payloadCryptoService;
    private final CryptoProperties cryptoProperties;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public GlobalCryptoAdvice(
            PayloadCryptoService payloadCryptoService,
            CryptoProperties cryptoProperties,
            ObjectMapper objectMapper) {
        this.payloadCryptoService = payloadCryptoService;
        this.cryptoProperties = cryptoProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(
            MethodParameter methodParameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public HttpInputMessage beforeBodyRead(
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) throws IOException {

        if (!shouldApply(inputMessage)) {
            return inputMessage;
        }

        JsonNode root = objectMapper.readTree(inputMessage.getBody());
        JsonNode payloadNode = root.get("payload");

        if (payloadNode == null || payloadNode.isNull() || payloadNode.asText().isBlank()) {
            throw new RuntimeException("Encrypted payload is required in request body as field 'payload'");
        }

        byte[] decryptedJson = payloadCryptoService.decryptToJsonBytes(payloadNode.asText());
        return new DecryptedHttpInputMessage(inputMessage.getHeaders(), decryptedJson);
    }

    @Override
    public Object afterBodyRead(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response) {

        if (!shouldApply(request) || body == null || body instanceof Resource) {
            return body;
        }

        if (isAlreadyEncryptedEnvelope(body)) {
            return body;
        }

        Map<String, String> encrypted = new HashMap<>();
        encrypted.put("payload", payloadCryptoService.encrypt(body));

        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            try {
                return objectMapper.writeValueAsString(encrypted);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize encrypted response", e);
            }
        }

        return encrypted;
    }

    private boolean shouldApply(HttpInputMessage inputMessage) {
        if (!cryptoProperties.isEnabled() || !(inputMessage instanceof ServletServerHttpRequest serverRequest)) {
            return false;
        }

        return !isExcluded(serverRequest.getServletRequest());
    }

    private boolean shouldApply(org.springframework.http.server.ServerHttpRequest request) {
        if (!cryptoProperties.isEnabled() || !(request instanceof ServletServerHttpRequest serverRequest)) {
            return false;
        }

        return !isExcluded(serverRequest.getServletRequest());
    }

    private boolean isExcluded(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String pattern : cryptoProperties.getExcludedPaths()) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAlreadyEncryptedEnvelope(Object body) {
        if (!(body instanceof Map<?, ?> map)) {
            return false;
        }
        Object value = map.get("payload");
        return value instanceof String str && !str.isBlank();
    }

    private static class DecryptedHttpInputMessage implements HttpInputMessage {

        private final HttpHeaders headers;
        private final byte[] decryptedBody;

        private DecryptedHttpInputMessage(HttpHeaders headers, byte[] decryptedBody) {
            this.headers = headers;
            this.decryptedBody = decryptedBody;
        }

        @Override
        public java.io.InputStream getBody() {
            return new ByteArrayInputStream(decryptedBody);
        }

        @Override
        public HttpHeaders getHeaders() {
            HttpHeaders updated = new HttpHeaders();
            updated.putAll(headers);
            updated.setContentLength(decryptedBody.length);
            updated.setContentType(MediaType.APPLICATION_JSON);
            return updated;
        }
    }
}
