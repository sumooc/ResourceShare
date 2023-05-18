package com.xiwei.contentcenter.feignclient;

import com.xiwei.contentcenter.domain.dto.content.ShareDTO;
import com.xiwei.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "UserCenter")
public interface UserCenterFeignClient {

    @GetMapping("/users/{id}")
    public UserDTO findById(@PathVariable Integer id);

}
