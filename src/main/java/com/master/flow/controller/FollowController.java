package com.master.flow.controller;

import com.master.flow.config.TokenProvider;
import com.master.flow.model.dao.UserDAO;

import com.master.flow.model.vo.Follow;
import com.master.flow.model.vo.User;
import com.master.flow.service.FollowService;

import com.master.flow.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    public int userCode(String token) {
        TokenProvider tokenProvider = new TokenProvider();
        User user = tokenProvider.validate(token);
        log.info(user.getUserPlatform());
        int followingCode = user.getUserCode();
        return followingCode;
    }

    // 프론트 쪽 팔로우 버튼 오류 방지 로직
    @GetMapping("/follow/status")
    public ResponseEntity status(@RequestParam(name = "followingUserCode") int followingUserCode,
                                 @RequestParam(name = "followerUserCode") int followerUserCode) {
        boolean isFollowing = followService.checkLogic(followingUserCode,followerUserCode);
        if(isFollowing) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    // 서비스에서 보낸 true false 값으로 정상연결 or 잘못된 연결
    @PostMapping("/follow")
    public ResponseEntity addFollowRelative(@RequestBody Follow follow) {
        boolean check = followService.addFollowRelative(follow.getFollowingUser().getUserCode(), follow.getFollowerUser().getUserCode());
        if(check)
        return ResponseEntity.status(HttpStatus.OK).build();
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 위와 마찬가지
    @DeleteMapping("/follow")
    public ResponseEntity unFollow(@RequestParam(name = "followingUserCode") int followingUserCode,
                                   @RequestParam(name = "followerUserCode") int followerUserCode) {
        boolean check = followService.unFollow(followingUserCode, followerUserCode);
        if(check) return ResponseEntity.status(HttpStatus.OK).build();
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 내가 팔로우한 팔로워들
    @GetMapping("/follow/myFollower/{followingUserCode}")
    public ResponseEntity viewMyFollower(@PathVariable(name = "followingUserCode") int followingUserCode) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.viewMyFollower(followingUserCode));
    }

    // 나를 팔로우한 유저들
    @GetMapping("/follow/toMe/{followerUserCode}")
    public ResponseEntity followMeUsers(@PathVariable(name= "followerUserCode") int followerUserCode) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.followMeUsers(followerUserCode));
    }

    // 데이터베이스 잘못 접근 전부 처리
    @ExceptionHandler(SQLException.class)
    public ResponseEntity handlerSQLException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 접근입니다");
    }
}
