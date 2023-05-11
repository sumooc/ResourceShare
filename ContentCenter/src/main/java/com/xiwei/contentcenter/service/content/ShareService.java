package com.xiwei.contentcenter.service.content;

import com.xiwei.contentcenter.dao.content.ShareMapper;
import com.xiwei.contentcenter.domain.dto.content.ShareDTO;
import com.xiwei.contentcenter.domain.dto.user.UserDTO;
import com.xiwei.contentcenter.domain.entity.content.Share;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ShareService {
    @Resource
    private ShareMapper shareMapper;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private DiscoveryClient discoveryClient;

    private static final String SERVICE_URL = "http://UserCenter";

    public ShareDTO findById(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();

        /*List<ServiceInstance> instances = discoveryClient.getInstances("UserCenter");
        List<String> targetUrls = instances.stream().map(instance -> instance.getUri().toString() + "/users/{id}").toList();
        int i = ThreadLocalRandom.current().nextInt(targetUrls.size());*/

        // 如果本地运行代码出现No instances available for xxx错误，查看是不是安有多个网卡使得nacos里服务的ip不对
        // 解决办法是在服务的application.yml中，加入配置：spring.cloud.inetutils: preferred-networks=192.168.0 #服务注册时优先使用这个网段。

        UserDTO userDTO = restTemplate.getForObject(SERVICE_URL + "/users/{userId}", UserDTO.class, userId);
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickName(userDTO.getWxNickname());

        return shareDTO;
    }
}
