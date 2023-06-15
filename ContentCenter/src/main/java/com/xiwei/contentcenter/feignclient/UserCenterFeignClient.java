package com.xiwei.contentcenter.feignclient;

import com.xiwei.contentcenter.configuration.UserCenterFeignConfiguration;
import com.xiwei.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// @FeignClient(name = "UserCenter")
@FeignClient(name = "UserCenter", configuration = UserCenterFeignConfiguration.class)
public interface UserCenterFeignClient {

    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);

}
