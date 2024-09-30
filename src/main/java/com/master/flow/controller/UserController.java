package com.master.flow.controller;

import com.master.flow.model.vo.User;
import com.master.flow.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class UserController {

    @Autowired
    private UserService userService;

    // 테스트용 코드
    @GetMapping("/showAllUser")
    public ResponseEntity showAllUser(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.showAllUser());
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody User vo){
        userService.registerUser(vo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 회원가입시 중복체크
    @GetMapping("/duplicateCheck")
    public ResponseEntity duplicateCheck(@RequestParam(name="userEmail") String userEmail, @RequestParam(name="userPlatform") String userPlatform){
        boolean check = userService.duplicateCheck(userEmail, userPlatform);

        System.err.println(check);

        return ResponseEntity.status(HttpStatus.OK).body(check);
    }
}
