package com.master.flow.controller;

import com.master.flow.model.vo.QPost;
import com.master.flow.service.*;
import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.vo.Post;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikesService likesService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private PostTagService postTagService;

    // 게시물 전체 조회
    @GetMapping("/post")
    public ResponseEntity viewAll(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            @RequestParam(name="keyword", required = false) String keyword) {

        Sort sortCondition;
        if ("oldest".equalsIgnoreCase(sort)) {
            sortCondition = Sort.by("postDate").ascending(); // 오래된 순 정렬
        } else {
            sortCondition = Sort.by("postDate").descending(); // 최신 순 정렬 (기본값)
        }

        Pageable pageable = PageRequest.of(page - 1, 10, sortCondition);

        BooleanBuilder builder = new BooleanBuilder();

        QPost qPost = QPost.post;

        if (keyword != null) {
            BooleanExpression expression = qPost.postDesc.like("%" + keyword + "%");
            builder.and(expression);
        }

        Page<Post> posts = postService.viewAll(builder, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(posts.getContent());
    }

    // 투표 게시판 게시물 전체 조회
    @GetMapping("/vote")
    public ResponseEntity postVoteViewAll(Post vo){
        // 추후 post_type = vote 만 조회 하도록 변경
        return ResponseEntity.status(HttpStatus.OK).body(postService.postVoteViewAll(vo));
    }

    // 좋아요한 게시물 조회
    @GetMapping("/post/liked/{userCode}")
    public ResponseEntity<List<Post>> getLikedPosts(@PathVariable("userCode") int userCode) {
        List<Post> likedPosts = likesService.getLikedPosts(userCode);
        return ResponseEntity.status(HttpStatus.OK).body(likedPosts);
    }

    // 좋아요 수 높은 순으로 게시물 조회
    @GetMapping("/post/ordered-by-likes")
    public ResponseEntity<List<Post>> viewAllOrderByLikes() {
        List<Post> likedPosts = likesService.viewAllOrderByLikes();
        return ResponseEntity.status(HttpStatus.OK).body(likedPosts);
    }

    // 태그로 게시물 조회
    @GetMapping("/post/tag/{tagName}")
    public ResponseEntity<List<Post>> getPostsByTag(@PathVariable("tagName") String tagName) {
        List<Post> posts = postTagService.viewPostsByTagName(tagName);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

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

    @DeleteMapping("/delPost")
    public ResponseEntity delPost(int postCode) {
        postService.delPost(postCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
