package com.lzp.example.consumer;

import com.lzp.example.common.model.User;
import com.lzp.example.common.service.UserService;
import com.lzp.rpc.proxy.ProxyFactory;
import com.lzp.rpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {

    public static void main(String[] args) {
        UserService userService = ProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("lzp");
        String userName = userService.getUserName(user);
        System.out.println(userName);
    }
}
