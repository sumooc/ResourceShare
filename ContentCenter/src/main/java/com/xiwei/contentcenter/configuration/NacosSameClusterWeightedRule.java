package com.xiwei.contentcenter.configuration;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.balancer.NacosBalancer;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.alibaba.nacos.common.utils.CollectionUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 同一集群优先调用
 * 自定义负载均衡实现需要实现ReactorServiceInstanceLoadBalancer接口以及重写choose方法
 */
@Slf4j
public class NacosSameClusterWeightedRule implements ReactorServiceInstanceLoadBalancer {

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Resource
    private NacosServiceManager nacosServiceManager;
    @Resource
    private NacosDiscoveryClient nacosDiscoveryClient;

    // loadbalancer 提供的访问当前服务的名称
    private final String serviceId;

    // loadbalancer 提供的访问的服务列表
    ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public NacosSameClusterWeightedRule(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    /**
     * 服务器调用负载均衡时调的方法
     * 此处代码内容与RandomLoadBalancer.java一致
     */
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        /*try {
            // 获取配置中的集群名称
            String clusterName = nacosDiscoveryProperties.getClusterName();
            List<String> services = nacosDiscoveryClient.getServices();

            // 获取服务相关的API
            NamingService namingService = nacosServiceManager.getNamingService();
            // 找到指定服务的所有实例 A
            List<Instance> instances = namingService.selectInstances("", true);
            // 过滤出相同集群下的所有实例 B
            List<Instance> sameClusterInstance = instances.stream().filter(instance ->
                    Objects.equals(instance.getClusterName(), clusterName)).collect(Collectors.toList());
            List<Instance> instancesToBeChosen = new ArrayList<>();
            // 如果B是空，则用A
            if (CollectionUtils.isEmpty(sameClusterInstance)) {
                instancesToBeChosen = instances;
                log.warn("发生跨集群调用，name={}，clusterName={}，instances={}", namingService, clusterName, instances);
            } else {
                instancesToBeChosen = sameClusterInstance;
            }
            // 基于权重的负载均衡算法，返回一个实例
        } catch (NacosException e) {
            log.error("NacosException:" + e);
        }
*/

        //注入的时候注入的是Lazy Provider，这里取出真正的Bean，也就是ServiceInstanceListSupplier
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        //获取实例列表
        return supplier.get(request).next().
                // 从列表中选择一个实例
                        map(serviceInstances -> processInstanceResponse(supplier, serviceInstances));
    }

    /**
     * 对负载均衡的服务进行筛选的方法
     * 此处代码内容与 RandomLoadBalancer 一致
     */
    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances);
        // 如果ServiceInstanceListSupplier也实现了SelectedInstanceCallback，则执行下面的逻辑进行回调。
        // SelectedInstanceCallback就是每次负载均衡器选择实例之后进行的回调。
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    /**
     * 对负载均衡的服务进行筛选的方法
     * 自定义
     * 此处的instances实例列表只会提供健康的实例所以不需要担心如果实例无法访问的情况
     */
    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }
        // 获取当前服务所在的集群名称
        String currentClusterName = nacosDiscoveryProperties.getClusterName();
        // 过滤在同一集群下注册的服务 根据集群名称筛选的集合
        List<ServiceInstance> sameClusterNameInstList = instances.stream().filter(i ->
                StringUtils.equals(i.getMetadata().get("nacos.cluster"), currentClusterName)).collect(Collectors.toList());
        ServiceInstance sameClusterNameInst;
        if (sameClusterNameInstList.isEmpty()) {
            // 如果为空，则根据权重直接过滤所有服务列表
            sameClusterNameInst = getHostByRandomWeight(instances);
        } else {
            // 如果不为空，则根据权重直接过滤所在集群下的服务列表
            sameClusterNameInst = getHostByRandomWeight(sameClusterNameInstList);
        }
        return new DefaultResponse(sameClusterNameInst);
    }

    private ServiceInstance getHostByRandomWeight(List<ServiceInstance> sameClusterNameInstList) {

        List<Instance> list = new ArrayList<>();
        Map<String, ServiceInstance> dataMap = new HashMap<>();
        // 此处将 ServiceInstance 转化为 Instance 是为了接下来调用nacos中的权重算法，由于入参不同，
        // 所以需要转换，此处建议打断点进行参数调试，以下是我目前为止所用到的参数，转化为map是为了最终方便获取取值到的服务对象
        sameClusterNameInstList.forEach(i -> {
            Instance ins = new Instance();
            Map<String, String> metadata = i.getMetadata();

            ins.setInstanceId(metadata.get("nacos.instanceId"));
            ins.setWeight(new BigDecimal(metadata.get("nacos.weight")).doubleValue());
            ins.setClusterName(metadata.get("nacos.cluster"));
            ins.setEphemeral(Boolean.parseBoolean(metadata.get("nacos.ephemeral")));
            ins.setHealthy(Boolean.parseBoolean(metadata.get("nacos.healthy")));
            ins.setPort(i.getPort());
            ins.setIp(i.getHost());
            ins.setServiceName(i.getServiceId());

            ins.setMetadata(metadata);

            list.add(ins);
            // key为服务ID，值为服务对象
            dataMap.put(metadata.get("nacos.instanceId"), i);
        });
        // 调用nacos官方提供的负载均衡权重算法
        Instance hostByRandomWeightCopy = ExtendBalancer.getHostByRandomWeightCopy(list);

        // 根据最终ID获取需要返回的实例对象
        return dataMap.get(hostByRandomWeightCopy.getInstanceId());
    }

}

class ExtendBalancer extends Balancer {
    /**
     * 根据权重选择随机选择一个
     */
    public static Instance getHostByRandomWeightCopy(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}
