package com.xiwei.usercenter.service.user;

import com.xiwei.usercenter.dao.user.UserMapper;
import com.xiwei.usercenter.domain.entity.user.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public User findById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
