package com.master.flow.controller;

import com.master.flow.model.dto.UserPostSummaryDTO;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.User;
import com.master.flow.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collection")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class CollectionController {

    @Autowired
    private CollectionService service;

    @PostMapping("/toggle/{postCode}")
    public ResponseEntity<Boolean> toggleCollection(@PathVariable("postCode") int postCode, @RequestBody User user) {
        Post post = new Post();
        post.setPostCode(postCode); // Post ID를 설정
        boolean isCollected = service.toggleCollectionWithoutUser(user, post);
        return ResponseEntity.status(HttpStatus.OK).body(isCollected);
    }

    @GetMapping("/{postCode}/count")
    public ResponseEntity<Integer> getCollectionCount(@PathVariable("postCode") int postCode) {
        Post post = new Post();
        post.setPostCode(postCode); // Post ID를 설정

        int collectionCount = service.countCollectionByPost(post); // 게시물의 좋아요 수 카운트
        return ResponseEntity.status(HttpStatus.OK).body(collectionCount);
    }

    // 유저가 저장한 게시물 조회
    @GetMapping("/{userCode}/collections")
    public ResponseEntity<UserPostSummaryDTO> getPostListByUser(@PathVariable("userCode") int userCode){
        UserPostSummaryDTO userPostSummaryDTO = service.getPostListByUser(userCode);
        return ResponseEntity.status(HttpStatus.OK).body(userPostSummaryDTO);
    }
}
