package com.master.flow.controller;

import com.master.flow.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/*")
public class FollowController {

    @Autowired
    private FollowService followService;
}
