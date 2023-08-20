package com.xiwei.contentcenter.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class MyRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 从请求参数中获取名为origin的参数并返回，如果获取不到origin参数则抛出异常
        String origin = request.getParameter("origin");
        if (StringUtil.isBlank(origin)) {
            throw new IllegalArgumentException("origin参数必须被指定");
        }
        return origin;
    }
}
