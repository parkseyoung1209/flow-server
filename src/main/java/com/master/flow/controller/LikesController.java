package com.master.flow.controller;

import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.dto.UserPostSummaryDTO;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import com.master.flow.model.vo.User;
import com.master.flow.service.LikesService;
import com.master.flow.service.PostImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class LikesController {

    @Autowired
    private LikesService likesService;

    @Autowired
    private PostImgService postImgService;

    @PostMapping("/toggle/{postCode}")
    public ResponseEntity<Boolean> toggleLike(@PathVariable("postCode") int postCode, @RequestBody User user) {
        Post post = new Post();
        post.setPostCode(postCode); // Post ID를 설정
        boolean isLiked = likesService.toggleLikeWithoutUser(user, post);
        return ResponseEntity.status(HttpStatus.OK).body(isLiked);
    }

    @GetMapping("/{postCode}/count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable("postCode") int postCode) {
        Post post = new Post();
        post.setPostCode(postCode); // Post ID를 설정

        int likeCount = likesService.countLikesByPost(post); // 게시물의 좋아요 수 카운트
        return ResponseEntity.status(HttpStatus.OK).body(likeCount);
    }

    // 유저가 좋아요한 게시물 조회
    @GetMapping("{userCode}/likes")
    public ResponseEntity<UserPostSummaryDTO> getPostListByUser(@PathVariable("userCode") int userCode){
        UserPostSummaryDTO userPostSummaryDTO = likesService.getPostListByUser(userCode);
        return ResponseEntity.status(HttpStatus.OK).body(userPostSummaryDTO);
    }

    // 좋아요 수 높은 순으로 게시물 조회
    @GetMapping("/post/ordered-by-likes")
    public ResponseEntity<List<PostDTO>> viewAllOrderByLikes() {
        List<Post> likedPosts = likesService.viewAllOrderByLikes();

        // 각 게시물에 대한 이미지 URL 추가
        List<PostDTO> postDTOS = new ArrayList<>();

        for (Post post : likedPosts) {
            List<PostImg> postImgs = postImgService.findByPost_PostCode(post.getPostCode());

            PostDTO postDTO = PostDTO.builder()
                    .postCode(post.getPostCode())
                    .postDesc(post.getPostDesc())
                    .userCode(post.getUser().getUserCode())
                    .user(post.getUser())
                    .imageUrls(postImgs.stream().map(PostImg::getPostImgUrl).collect(Collectors.toList()))
                    .build();
            postDTOS.add(postDTO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(postDTOS);
    }

    //    좋아요 한개 삭제
    @DeleteMapping("/delLike")
    public ResponseEntity delLike(@RequestParam("postCode") int postCode) {
        likesService.delLike(postCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
