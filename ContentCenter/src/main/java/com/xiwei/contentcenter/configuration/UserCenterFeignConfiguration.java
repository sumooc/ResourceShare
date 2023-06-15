package com.xiwei.contentcenter.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * Java代码方式打印feign的日志
 */
public class UserCenterFeignConfiguration {
    @Bean
    public Logger.Level level() {
        // 让feign打印所有请求的细节
        return Logger.Level.FULL;
    }

    /**
     * 还有以下配置
     * Retryer 指定重试策略
     * ErrorDecoder 指定错误解码器
     * Request.Options 超时时间
     * Collection<RequestInterceptor> 拦截器
     * SetterFactory 用于设置Hystrix的配置属性，Feign整合Hystrix才会用
     */
}
