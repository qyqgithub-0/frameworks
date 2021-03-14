package com.demo.service;

import com.spring.annotation.Autowired;
import com.spring.annotation.Component;

/**
 * @author rkc
 * @date 2021/3/12 13:40
 */
@Component("indexService")
public class IndexService {

    @Autowired
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }
}
