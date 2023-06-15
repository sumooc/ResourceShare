package com.xiwei.contentcenter.service.content;

import com.xiwei.contentcenter.dao.content.ShareMapper;
import com.xiwei.contentcenter.domain.dto.content.ShareDTO;
import com.xiwei.contentcenter.domain.dto.user.UserDTO;
import com.xiwei.contentcenter.domain.entity.content.Share;
import com.xiwei.contentcenter.feignclient.UserCenterFeignClient;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserCenterFeignClient userCenterFeignClient;

    private static final String SERVICE_URL = "http://UserCenter";

    public ShareDTO findById(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();

        /*List<ServiceInstance> instances = discoveryClient.getInstances("UserCenter");
        List<String> targetUrls = instances.stream().map(instance -> instance.getUri().toString() + "/users/{id}").toList();
        int i = ThreadLocalRandom.current().nextInt(targetUrls.size());*/

        // UserDTO userDTO = restTemplate.getForObject(SERVICE_URL + "/users/{userId}", UserDTO.class, userId);
        // 上面的代码使用openFeign后,改成如下
        UserDTO userDTO = userCenterFeignClient.findById(userId);

        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickName(userDTO.getWxNickname());

        return shareDTO;
    }
}
