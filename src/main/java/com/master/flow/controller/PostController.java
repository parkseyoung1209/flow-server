package com.master.flow.controller;

import com.master.flow.service.LikesService;
import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import com.master.flow.model.vo.Product;
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
//    @GetMapping("/post/ordered-by-likes")
//    public ResponseEntity<List<Post>> getPostsOrderedByLikes() {
//        List<Post> likedPosts = postService.getPostsOrderedByLikes();
//        return ResponseEntity.status(HttpStatus.OK).body(likedPosts);
//    }

    @GetMapping("/post/liked/{userCode}")
    public ResponseEntity<List<Post>> getLikedPosts(@PathVariable("userCode") int userCode) {
        List<Post> likedPosts = likesService.getLikedPosts(userCode);
        return ResponseEntity.status(HttpStatus.OK).body(likedPosts);
    }
    // 게시물 좋아요 순으로 조회
    /*
    *  전송하는 방법이..!
    * 자바스크립트에서 보내는 방법!
    * 그때 파일을 같이 보내야 할 때는 FormData 객체 생성해서
    * 각각의 값들 append로 추가해서 마지막에 보내기만 하면 끝!
    * */


    // 게시물 업로드
    @PostMapping("/post")
    public ResponseEntity upload(@RequestBody PostDTO postDto){
        //front에서 user정보 받아서 DTO로 모두 받기 -> Sevice에서 각자 save
        // user 설정, img 추가 필요
        
        System.out.println(postDto);
//        postService.change(postDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    };

    // 게시물 수정
    @PutMapping("/post")
    public ResponseEntity update(@RequestBody PostDTO postDto){

        postService.change(postDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    };

}
