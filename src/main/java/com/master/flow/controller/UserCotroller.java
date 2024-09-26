package com.master.flow.controller;

import com.master.flow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/*")
public class UserCotroller {

    @Autowired
    private UserService userService;

}
