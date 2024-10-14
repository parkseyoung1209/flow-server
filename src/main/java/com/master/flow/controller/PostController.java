package com.master.flow.controller;

import com.master.flow.model.dao.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Autowired
    private PostReportDAO postReportDAO;
    @Autowired
    private CommentDAO commentDAO;
    @Autowired
    private LikesDAO likesDAO;
    @Autowired
    private CommentReportDAO commentReportDAO;
    @Autowired
    private PostImgDAO postImgDAO;
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private CollectionDAO collectionDAO;
    @Autowired
    private PostTagDAO postTagDAO;
    @Autowired
    private VoteDAO voteDAO;
    @Autowired
    private CommentReportService commentReportService;

    @GetMapping("/post")
    public ResponseEntity<List<PostDTO>> viewAll(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            @RequestParam(name = "keyword", required = false) String keyword) {

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

        // 각 게시물에 대한 이미지 URL 추가
        List<PostDTO> postDTOS = new ArrayList<>();

        for (Post post : posts) {
            List<PostImg> postImgs = postImgService.findByPost_PostCode(post.getPostCode());

            PostDTO postDTO = PostDTO.builder()
                    .postCode(post.getPostCode())
                    .postDesc(post.getPostDesc())
                    .userCode(post.getUser().getUserCode())
                    .imageUrls(postImgs.stream().map(PostImg::getPostImgUrl).collect(Collectors.toList()))
                    .build();
            postDTOS.add(postDTO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(postDTOS);
    }


    // 카테고리별 게시물 조회
    @GetMapping("/category")
    public ResponseEntity<List<Post>> getPostsByFilters(
            @RequestParam(name = "job", required = false) String job,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "height", required = false) Integer height) {
        List<Post> posts = postService.findPostsByFilters(job, gender, height);
        return ResponseEntity.ok(posts);
    }

    // 게시물 1개 보기 ( 상세페이지 조회)
    @GetMapping("/post/{postCode}")
    public ResponseEntity view(@PathVariable(name="postCode") int postCode) {
        return ResponseEntity.ok(postService.view(postCode));
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


//        log.info("products : " + postDTO.getProducts());
//        log.info("tags : " + postDTO.getTagCodes());
        log.info("postPublicYN : " + postDTO.getPostPublicYn());


        List<MultipartFile> files = postDTO.getImageFiles();
        List<Product> products = postDTO.getProducts();
        List<Integer> tags = postDTO.getTagCodes();

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

                // 모든 칸이 비어있을때 제외
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

    // 게시물 수정
    @PutMapping("/post/{postCode}")
    public ResponseEntity update(@RequestBody PostDTO postDTO){

        // 조회한 게시물의 userCode === 수정하는 userCode (client)
        // postCode

        int postCode = postDTO.getPostCode();
        
        // 기존 postDTO 불러오기
        Post prePost = postService.view(postCode);
        List<PostImg> prefiles = postImgService.findByPost_PostCode(postCode);
        List<Product> preproducts = productService.certainProduct(postCode);
        List<PostTag> pretags = postTagService.certainPostTag(postCode);

        // 기존 이미지 삭제 -> 새로 업로드 및 저장

        // 수정된 postDTO
        List<MultipartFile> files = postDTO.getImageFiles();
        List<Product> products = postDTO.getProducts();
        List<Integer> tags = postDTO.getTagCodes();
        
        // post
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
        // 이미 글이 신고되어있는 상태일때
        postReportDAO.deletePostReportByPostCode(postCode);

        // 그 글에 있는 댓글들 중 신고된 댓글
        List<Comment> comments = commentDAO.findByPostCode(postCode);
        List<CommentReport> commentsReport = commentReportService.showAllCommentReport();
        for(Comment comment : comments) {
            for(CommentReport commentReport : commentsReport) {
                if(commentReport.getComment().getCommentCode() == comment.getCommentCode()) {
                    commentReportService.delCommentReport(commentReport.getCommentReportCode());
                }
            }
        }

        // 그 글에 있는 댓글들 삭제
        commentDAO.deleteCommentByPostCode(postCode);

        // 좋아요가 되어있을때
        likesDAO.deleteLikesByPostCode(postCode);

        // 글에 있는 사진들 삭제
        postImgDAO.deletePostImgByPostCode(postCode);

        // 글에 등록된 상품들 삭제
        productDAO.deleteProductByPostCode(postCode);

        // 그 글이 유저가 저장한 글일 경우
        collectionDAO.deleteCollectionByPostCode(postCode);

        // 글에 적용된 태그들 삭제
        postTagDAO.deletePostTagByPostCode(postCode);

        // 투표 글인 경우
        voteDAO.deleteVoteByPostCode(postCode);

        postService.delPost(postCode);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
