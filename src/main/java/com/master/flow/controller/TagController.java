package com.master.flow.controller;

import com.master.flow.model.vo.Post;
import com.master.flow.service.PostTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class TagController {

    @Autowired
    private PostTagService postTagService;

    // 여러 태그로 게시물 조회
    @GetMapping("/tags/posts")
    public ResponseEntity<List<Post>> getPostsBySelectedTags(@RequestParam("tagCodes") List<Integer> tagCodes) {
        List<Post> posts = postTagService.viewPostsByTags(tagCodes);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }
}
