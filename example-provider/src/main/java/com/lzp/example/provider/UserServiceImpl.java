package com.lzp.example.provider;

import com.lzp.example.common.model.User;
import com.lzp.example.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public String getUserName(User user) {
        return user.getName() + " from provider";
    }
}
