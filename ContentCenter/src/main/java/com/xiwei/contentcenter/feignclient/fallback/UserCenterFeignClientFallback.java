package com.xiwei.contentcenter.feignclient.fallback;

import com.xiwei.contentcenter.domain.dto.user.UserDTO;
import com.xiwei.contentcenter.feignclient.UserCenterFeignClient;
import org.springframework.stereotype.Component;

/**
 * feign整合sentinel
 */
@Component
public class UserCenterFeignClientFallback implements UserCenterFeignClient {
    @Override
    public UserDTO findById(Integer id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setWxNickname("王五");
        return userDTO;
    }
}
