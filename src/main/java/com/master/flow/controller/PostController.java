package com.master.flow.controller;

import com.master.flow.model.vo.*;
import com.master.flow.service.*;
import com.master.flow.model.dto.PostDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    private TagService tagService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PostImgService postImgService;
    @Autowired
    private PostTagService postTagService;

    // 게시물 전체 조회
    @GetMapping("/post")
    public ResponseEntity<List<Post>> viewAll(@RequestParam(required = false) String sort) {
        List<Post> posts = postService.viewAll(sort);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    // 투표 게시판 게시물 전체 조회
    @GetMapping("/vote")
    public ResponseEntity postVoteViewAll(Post vo){
        // 추후 post_type = vote 만 조회 하도록 변경
        return ResponseEntity.status(HttpStatus.OK).body(postService.postVoteViewAll(vo));
    }

    // 좋아요한 게시물 조회
//    @GetMapping("/post/ordered-by-likes")
//    public ResponseEntity<List<Post>> getPostsOrderedByLikes() {
//        List<Post> likedPosts = postService.getPostsOrderedByLikes();
//        return ResponseEntity.status(HttpStatus.OK).body(likedPosts);
//    }

    // 좋아요한 게시물 조회
    @GetMapping("/post/liked/{userCode}")
    public ResponseEntity<List<Post>> getLikedPosts(@PathVariable("userCode") int userCode) {
        List<Post> likedPosts = likesService.getLikedPosts(userCode);
        return ResponseEntity.status(HttpStatus.OK).body(likedPosts);
    }

    // 좋아요 수 높은 순으로 게시물 조회
//    @GetMapping("/post/ordered-by-likes")
//    public ResponseEntity<List<Post>> viewAllOrderByLikes() {
//        List<Post> likedPosts = likesService.viewAllOrderByLikes();
//        return ResponseEntity.status(HttpStatus.OK).body(likedPosts);
//    }

    // 태그로 게시물 조회
//    @GetMapping("/post/tag/{tagName}")
//    public ResponseEntity<List<Post>> getPostsByTag(@PathVariable("tagName") String tagName) {
//        List<Post> posts = tagService.viewPostsByTag(tagName);
//        return ResponseEntity.status(HttpStatus.OK).body(posts);
//    }


    // 업로드 경로
    private String path = "\\\\192.168.10.51\\flow\\";

    // 게시물 업로드
    @PostMapping("/post")
    public ResponseEntity upload(PostDTO postDTO) throws IOException {

//        log.info("products : " + postDTO.getProducts());
//        log.info("tags : " + postDTO.getTagCodes());

        List<MultipartFile> files = postDTO.getImageFiles();
        List<Product> products = postDTO.getProducts();
        List<Integer> tags = postDTO.getTagCodes();

        /*
         * 자바스크립트에서 보내는 방법!
         * 그때 파일을 같이 보내야 할 때는 FormData 객체 생성해서
         * 각각의 값들 append로 추가해서 마지막에 보내기만 하면 끝!
         * */

        // post 업로드
        Post post = postService.save(Post.builder()
                        .postType("post")
                        .postDesc(postDTO.getPostDesc())
                        .postDate(LocalDateTime.now())
                        .postPublicYn(postDTO.getPostPublicYn())
                        .user(userService.findUser(postDTO.getUserCode()))
                .build());

        int postCode = post.getPostCode();

        // 제품추가
        for(Product p : products) {
            Product product = productService.addProduct(Product.builder()
                            .productBrand(p.getProductBrand())
                            .productName(p.getProductName())
                            .productSize(p.getProductSize())
                            .productBuyFrom(p.getProductBuyFrom())
                            .productLink(p.getProductLink())
                            .post(Post.builder()
                                    .postCode(post.getPostCode())
                                    .build())
                    .build());
            //log.info("product : " + product);
        }

        // 이미지 DB 저장
        for(MultipartFile f : files) {
            // 파일 업로드
            String uuid = UUID.randomUUID().toString();

            String fileName = uuid + "_" + f.getOriginalFilename();
            //http://192.168.10.51:8082/postImg/파일
            File imageFile = new File(path + "postImg" + File.separator + fileName);

            // 파일에 저장
            f.transferTo(imageFile);

            // DB저장
            PostImg postImg = postImgService.addImg(PostImg.builder()
                            .post(post.builder()
                                    .postCode(post.getPostCode())
                                    .build())
                            .postImgUrl("http://192.168.10.51:8081/postImg" + File.separator + fileName)
                    .build());
        }

        // PostTag 저장
        for(Integer num : tags){
//            log.info("num : " + num);
            PostTag postTag = postTagService.addTag(PostTag.builder()
                            .post(Post.builder()
                                    .postCode(postCode)
                                    .build())
                            .tag(Tag.builder()
                                    .tagCode(num)
                                    .build())
                    .build());
//            log.info("postTag : " + postTag);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    };

    // 게시물 내용/공개여부 수정
    @PutMapping("/post")
    public ResponseEntity update(@RequestBody PostDTO postDTO){

        List<MultipartFile> files = postDTO.getImageFiles();
        List<Product> products = postDTO.getProducts();
        List<Integer> tags = postDTO.getTagCodes();

        Post post = postService.save(Post.builder()
                        .postCode(postDTO.getPostCode())
                        .postType("post")
                        .postDesc(postDTO.getPostDesc())
                        .postDate(LocalDateTime.now())
                        .postPublicYn(postDTO.getPostPublicYn())
                .build());

        return ResponseEntity.status(HttpStatus.OK).build();
    };


    @DeleteMapping("/post/{postCode}")
    public ResponseEntity delPost(@PathVariable("postCode") int postCode) {
        //log.info("POST_CODE : "+ postCode);

        // 태그삭제
        postTagService.deleteAll(postCode);

        // 이미지 삭제
        postImgService.deleteAll(postCode);
        
        // 제품들 삭제
        productService.deleteAll(postCode);
        
        // 게시글 삭제
        postService.delPost(postCode);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
