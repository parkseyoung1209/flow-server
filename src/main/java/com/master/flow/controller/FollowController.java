package com.master.flow.controller;

import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.dto.PostInfoDTO;
import com.master.flow.model.vo.Follow;
import com.master.flow.model.vo.PostImg;
import com.master.flow.service.FollowService;
import com.master.flow.service.PostImgService;
import com.master.flow.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostImgService postImgService;

    // 프론트 쪽 팔로우 버튼 오류 방지 로직
    @GetMapping("private/follow/status")
    public Boolean status(@RequestParam(name = "followingUserCode") int followingUserCode,
                                 @RequestParam(name = "followerUserCode") int followerUserCode) {
        boolean isFollowing = followService.checkLogic(followingUserCode,followerUserCode);
        if(isFollowing) {
            return true;
        } else {
            return false;
        }
    }

    // 서비스에서 보낸 true false 값으로 정상연결 or 잘못된 연결
    @PostMapping("private/follow")
    public ResponseEntity addFollowRelative(@RequestBody Follow follow) {
        boolean check = followService.addFollowRelative(follow.getFollowingUser().getUserCode(), follow.getFollowerUser().getUserCode());
        if(check)
       return ResponseEntity.status(HttpStatus.OK).build();
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 위와 마찬가지
    @DeleteMapping("private/follow")
    public ResponseEntity unFollow(@RequestParam(name = "followingUserCode") int followingUserCode,
                                   @RequestParam(name = "followerUserCode") int followerUserCode) {
        boolean check = followService.unFollow(followingUserCode, followerUserCode);
        if(check) return ResponseEntity.status(HttpStatus.OK).build();
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 내가 팔로우한 팔로워들
    @GetMapping("private/follow/myFollower/{followingUserCode}")
    public ResponseEntity viewMyFollower(@PathVariable(name = "followingUserCode") int followingUserCode, @RequestParam(name = "key", required = false) String key) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.viewMyFollower(followingUserCode, key));
    }

    // 나를 팔로우한 유저들
    @GetMapping("private/follow/toMe/{followerUserCode}")
    public ResponseEntity followMeUsers(@PathVariable(name= "followerUserCode") int followerUserCode, @RequestParam(name = "key", required = false) String key) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.followMeUsers(followerUserCode, key));
    }

    // 데이터베이스 잘못 접근 전부 처리
    @ExceptionHandler(SQLException.class)
    public ResponseEntity handlerSQLException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 접근입니다");
    }

    // 내가 팔로우한 유저의 게시글 조회
    @GetMapping("/posts/following/{userCode}")
    public ResponseEntity<List<PostDTO>> getPostsFromFollowedUsers(
            @PathVariable(name = "userCode") int userCode) {

        Sort sortCondition = Sort.by("postDate").descending(); // 최신 순 정렬
        
        List<PostInfoDTO> postInfoList = followService.getPostsFromFollowingUsers(userCode);

        List<PostDTO> postDTOS = postInfoList.stream().map(postInfo -> {
            List<PostImg> postImgs = postImgService.findByPost_PostCode(postInfo.getPost().getPostCode());

            return PostDTO.builder()
                    .postCode(postInfo.getPost().getPostCode())
                    .postDesc(postInfo.getPost().getPostDesc())
                    .userCode(postInfo.getPost().getUser().getUserCode())
                    .imageUrls(postImgs.stream().map(PostImg::getPostImgUrl).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(postDTOS);
    }

}
