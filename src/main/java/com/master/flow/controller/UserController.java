package com.master.flow.controller;

import com.master.flow.config.TokenProvider;
import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.vo.User;
import com.master.flow.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserDAO userDAO;

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
        return ResponseEntity.status(HttpStatus.OK).body(check);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User vo){
        User user = userService.login(vo.getUserEmail(), vo.getUserPlatform());
        String token = tokenProvider.create(user);
        int banCount = user.getUserBanCount();

//        밴 상태일때 로그인 금지 (4회 이상은 그냥 금지)
        if(user.getUserBanStatus().equals("Y")) {
            if(banCount == 1) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusDays(7));
                if(duration.isPositive()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
                } else if(duration.isNegative()) {
                    user.setUserBanStatus("N");
                    user.setUserBanDate(null);
                    userDAO.save(user);
                    return ResponseEntity.status(HttpStatus.OK).body(token);
                }
            } else if(banCount == 2) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusDays(15));
                if(duration.isPositive()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
                } else if(duration.isNegative()) {
                    user.setUserBanStatus("N");
                    user.setUserBanDate(null);
                    userDAO.save(user);
                    return ResponseEntity.status(HttpStatus.OK).body(token);
                }
            } else if(banCount == 3) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusMinutes(1));
                if(duration.isPositive()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
                } else if(duration.isNegative()) {
                    user.setUserBanStatus("N");
                    user.setUserBanDate(null);
                    userDAO.save(user);
                    return ResponseEntity.status(HttpStatus.OK).body(token);
                }
            } else if(banCount >= 4) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
            }
        } else if(user.getUserBanStatus().equals("N")) {
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

//    유저 신고하기
    @PutMapping("/banUser")
    public ResponseEntity banUser(@RequestParam(name="userCode") int userCode) {
        userService.banUser(userCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
