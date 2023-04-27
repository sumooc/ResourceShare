package com.xiwei.contentcenter.service.content;

import com.xiwei.contentcenter.dao.content.ShareMapper;
import com.xiwei.contentcenter.domain.dto.content.ShareDTO;
import com.xiwei.contentcenter.domain.dto.user.UserDTO;
import com.xiwei.contentcenter.domain.entity.content.Share;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ShareService {
    @Resource
    private ShareMapper shareMapper;
    @Resource
    private RestTemplate restTemplate;

    public ShareDTO findById(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();
        UserDTO userDTO = restTemplate.getForObject("http://127.0.0.1:8080/users/{id}", UserDTO.class, userId);

        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickName(userDTO.getWxNickname());

        return shareDTO;
    }
}
