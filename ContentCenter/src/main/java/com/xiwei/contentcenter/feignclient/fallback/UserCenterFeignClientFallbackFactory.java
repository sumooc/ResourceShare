package com.xiwei.contentcenter.feignclient.fallback;

import com.xiwei.contentcenter.domain.dto.user.UserDTO;
import com.xiwei.contentcenter.feignclient.UserCenterFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCenterFeignClientFallbackFactory implements FallbackFactory<UserCenterFeignClient> {
    @Override
    public UserCenterFeignClient create(Throwable cause) {
        return id -> {
            log.warn("远程调用被限流/降级了", cause);
            UserDTO userDTO = new UserDTO();
            userDTO.setWxNickname("王五");
            return userDTO;
        };
    }
}
