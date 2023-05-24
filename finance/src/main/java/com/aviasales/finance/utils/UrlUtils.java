package com.aviasales.finance.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class UrlUtils {

    public String getRootUrl() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String rootUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return rootUrl;
    }
}
