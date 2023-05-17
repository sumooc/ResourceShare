package com.xiwei.contentcenter.configuration;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
public class CustomLoadBalancerConfiguration {
    @Bean
    ReactorLoadBalancer<ServiceInstance> reactorLoadBalancer(Environment environment,
                                                            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        /*// 随机负载均衡方式
        return new RandomLoadBalancer(loadBalancerClientFactory
                .getLazyProvider(name, ServiceInstanceListSupplier.class), name);*/
        // 轮询负载均衡方式
        /*return new RoundRobinLoadBalancer(loadBalancerClientFactory.
                getLazyProvider(name,ServiceInstanceListSupplier.class),name);*/

        // 使用自定义的方式
        return new NacosSameClusterWeightedRule(loadBalancerClientFactory.
                getLazyProvider(name,ServiceInstanceListSupplier.class),name);
    }
}
