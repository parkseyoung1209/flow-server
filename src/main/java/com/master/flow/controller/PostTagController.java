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
public class PostTagController {

    @Autowired
    private PostTagService postTagService;

    // 태그 코드로 게시물 조회
    @GetMapping("/post/tagCode/{tagCode}")
    public ResponseEntity<List<Post>> getPostsByTagCode(@PathVariable("tagCode") int tagCode) {
        List<Post> posts = postTagService.viewPostsByTagCode(tagCode);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    // postCode로 tagCode 조회
    @GetMapping("postTag/{postCode}")
    public ResponseEntity getPostTags(@PathVariable(name = "postCode") int postCode) {

        List<Integer> tagCodes = postTagService.findPostTag(postCode);
//        System.out.println(tagCodes);

        return ResponseEntity.ok(tagCodes);
    }

}
