package com.resume.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.resume.entity.User;
import com.resume.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    /**
     * 根据用户名查询用户
     */
    public User getByUsername(String username) {
        return lambdaQuery().eq(User::getUsername, username).one();
    }
}
