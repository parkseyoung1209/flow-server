package com.master.flow.controller;

import com.master.flow.model.vo.Follow;
import com.master.flow.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class FollowController {

    @Autowired

    private FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity addFollowingRelative(Follow value) {
        boolean logic = followService.addFollowingRelative(value);
        // 서비스의 불리언 값에 따라서 200인지 400인지 판단
        if(logic == true) return ResponseEntity.status(HttpStatus.OK).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/follow")
    public ResponseEntity unFollow(Follow value) {
        followService.unFollow(value);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/follow")
    public ResponseEntity followBack(Follow value) {
        boolean logic = followService.FollowBack(value);
        if(logic) return ResponseEntity.status(HttpStatus.OK).build();
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @GetMapping("/follow")
    public ResponseEntity viewAllFollowList() {
        return ResponseEntity.status(HttpStatus.OK).body(followService.viewAllFollowList());
    }
}
