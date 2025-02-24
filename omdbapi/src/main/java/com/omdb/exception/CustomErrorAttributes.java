package com.omdb.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> defaultAttributes = super.getErrorAttributes(webRequest, options);
        Map<String, Object> errorAttributes = new HashMap<>();


        errorAttributes.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        errorAttributes.put("status", defaultAttributes.get("status"));
        errorAttributes.put("message", defaultAttributes.get("message"));
        errorAttributes.put("exception", defaultAttributes.get("exception"));
        errorAttributes.put("locale", webRequest.getLocale().toString());
        errorAttributes.put("requestId", UUID.randomUUID().toString());

        errorAttributes.remove("error");
        errorAttributes.remove("path");
        errorAttributes.remove("trace");

        return errorAttributes;
    }
}