package com.master.flow.controller;

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
    public ResponseEntity<Boolean> toggleCollection(@PathVariable int postCode, @RequestBody User user) {
        Post post = new Post();
        post.setPostCode(postCode); // Post ID를 설정
        boolean isCollected = service.toggleCollectionWithoutUser(user, post);
        return ResponseEntity.status(HttpStatus.OK).body(isCollected);
    }

    @GetMapping("/{postCode}/count")
    public ResponseEntity<Integer> getCollectionCount(@PathVariable int postCode) {
        Post post = new Post();
        post.setPostCode(postCode); // Post ID를 설정

        int collectionCount = service.countCollectionByPost(post); // 게시물의 좋아요 수 카운트
        return ResponseEntity.status(HttpStatus.OK).body(collectionCount);
    }
}
