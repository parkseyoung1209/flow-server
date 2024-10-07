package com.master.flow.controller;

import com.master.flow.model.dto.UserPostSummaryDTO;
import com.master.flow.model.vo.*;
import com.master.flow.service.*;
import com.master.flow.model.dto.PostDTO;
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
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PostImgService postImgService;
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

    // 카테고리별 게시물 조회
    @GetMapping("/category")
    public ResponseEntity<List<Post>> getPostsByFilters(
            @RequestParam(required = false) String job,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer height) {
        List<Post> posts = postService.findPostsByFilters(job, gender, height);
        return ResponseEntity.ok(posts);
    }

    // 투표 게시판 게시물 전체 조회
    @GetMapping("/postVote")
    public ResponseEntity postVoteViewAll(Post vo){
        return ResponseEntity.status(HttpStatus.OK).body(postService.postVoteViewAll(vo));
    }

    // 투표 게시물 조회
    @GetMapping("/postVote/{postCode}")
    public ResponseEntity votePostView(@PathVariable(name="postCode") int postCode){
        return ResponseEntity.status(HttpStatus.OK).body(postService.votePostView(postCode));
    }
    
    // 해당 유저가 만든 게시물 조회
    @GetMapping("/{userCode}/post")
    public ResponseEntity<UserPostSummaryDTO> getPostListByUser(@PathVariable("userCode") int userCode){
        UserPostSummaryDTO userPostSummaryDTO = postService.getPostListByUser(userCode);
        return ResponseEntity.status(HttpStatus.OK).body(userPostSummaryDTO);
    }

    // 업로드 경로
    private String path = "\\\\192.168.10.51\\flow\\";

    // 게시물 업로드
    @PostMapping("/post")
    public ResponseEntity upload(PostDTO postDTO) throws IOException {

        // 유저코드 받기!
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
        Post post = new Post();

        // post 업로드
        if( postDTO.getPostDesc() != null && !postDTO.getPostDesc().isEmpty()) {
            post = postService.save(Post.builder()
                    .postType("post")
                    .postDesc(postDTO.getPostDesc())
                    .postDate(LocalDateTime.now())
                    .postPublicYn(postDTO.getPostPublicYn())
                    .user(userService.findUser(postDTO.getUserCode()))
                    .build());
        } else {
            post = postService.save(Post.builder()
                    .postType("post")
                    .postDate(LocalDateTime.now())
                    .postPublicYn(postDTO.getPostPublicYn())
                    .user(userService.findUser(postDTO.getUserCode()))
                    .build());
        }

        int postCode = post.getPostCode();

        // 제품추가
        if(products!=null && !products.isEmpty()) {
            for (Product p : products) {

                boolean allEmpty = p.getProductBrand().isEmpty() &&
                        p.getProductName().isEmpty() &&
                        p.getProductSize().isEmpty() &&
                        p.getProductBuyFrom().isEmpty() &&
                        p.getProductLink().isEmpty();


                if(!allEmpty) {
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
            }
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
        if(tags != null ){
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

    // 게시물 삭제
    @DeleteMapping("/post/{postCode}")
    public ResponseEntity delPost(@PathVariable("postCode") int postCode) {
        //log.info("POST_CODE : "+ postCode);

        // 조건필요 - USER가 동일할 경우 (postCode로 userCode 가져오기)

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
