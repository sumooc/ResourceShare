package com.xiwei.contentcenter.service.content;

import com.xiwei.contentcenter.dao.content.ShareMapper;
import com.xiwei.contentcenter.domain.dto.content.ShareDTO;
import com.xiwei.contentcenter.domain.dto.user.UserDTO;
import com.xiwei.contentcenter.domain.entity.content.Share;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.ServiceInstance;
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

    public ShareDTO findById(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();

        List<ServiceInstance> instances = discoveryClient.getInstances("UserCenter");
        String targetUrl = instances.stream().map(instance -> instance.getUri().toString() + "/users/{id}")
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("当前没有实例"));
        UserDTO userDTO = restTemplate.getForObject(targetUrl, UserDTO.class, userId);
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickName(userDTO.getWxNickname());

        return shareDTO;
    }
}
