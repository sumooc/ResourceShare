package com.xiwei.contentcenter.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.util.Arrays;

@Component
@Slf4j
public class MyUrlCleaner implements UrlCleaner {
    @Override
    public String clean(String originUrl) {
        log.info("originUrl = {}", originUrl);
        String[] split = originUrl.split("/");
        return Arrays.stream(split).map(s -> {
            return "{id}";
        }).reduce((a, b) -> a + "/" + b).orElse("");
    }
}
