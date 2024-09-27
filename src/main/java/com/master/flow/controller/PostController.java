package com.master.flow.controller;

import com.master.flow.model.vo.Post;
import com.master.flow.service.LikesService;
import com.master.flow.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikesService likesService;

    // 게시물 전체 조회
    @GetMapping("/post")
    public ResponseEntity<List<Post>> viewAll(@RequestParam(required = false) String sort) {
        List<Post> posts = postService.viewAll(sort);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }
    // 좋아요한 게시물 조회
    @GetMapping("/post/liked/{userCode}")
    public ResponseEntity<List<Post>> getLikedPosts(@PathVariable("userCode") int userCode) {
        List<Post> likedPosts = likesService.getLikedPosts(userCode);
        return ResponseEntity.status(HttpStatus.OK).body(likedPosts);
    }

    // 게시물 좋아요 순으로 조회
    @GetMapping("/post/ordered-by-likes")
    public ResponseEntity<List<Post>> getPostsOrderedByLikes() {
        List<Post> likedPosts = postService.getPostsOrderedByLikes();
        return ResponseEntity.status(HttpStatus.OK).body(likedPosts);
    }
}
