package com.peregi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peregi.entity.User;
import com.peregi.mapper.UserMapper;
import com.peregi.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
}
