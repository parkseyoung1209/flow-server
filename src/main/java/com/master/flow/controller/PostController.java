package com.master.flow.controller;

import com.master.flow.model.dao.*;
import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.dto.PostImgDTO;
import com.master.flow.model.dto.UserPostSummaryDTO;
import com.master.flow.model.vo.*;
import com.master.flow.service.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private LikesService likesService;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private TagService tagService;
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
    @Autowired
    private VoteService voteService;
    @Autowired
    private VoteDescService voteDescService;

    // 컨트롤러에서 페이징 파라미터 추가
    @GetMapping("/post")
    public ResponseEntity<Page<PostDTO>> viewAll(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Sort sortCondition = Sort.by("postDate").descending();
        BooleanBuilder builder = new BooleanBuilder();
        QPost qPost = QPost.post;

        if (keyword != null) {
            BooleanExpression expression = qPost.postDesc.like("%" + keyword + "%");
            builder.and(expression);
        }

        // postType이 "vote"가 아닌 게시물만 조회
        builder.and(qPost.postType.ne("vote"));

        // 페이징 처리
        Page<Post> posts = postService.viewAll(builder, PageRequest.of(page, size, sortCondition));

        // 각 게시물에 대한 이미지 URL 추가
        Page<PostDTO> postDTOS = posts.map(post -> {
            List<PostImg> postImgs = postImgService.findByPost_PostCode(post.getPostCode());
            return PostDTO.builder()
                    .postCode(post.getPostCode())
                    .postDesc(post.getPostDesc())
                    .userCode(post.getUser().getUserCode())
                    .user(post.getUser())
                    .imageUrls(postImgs.stream().map(PostImg::getPostImgUrl).collect(Collectors.toList()))
                    .build();
        });

        System.err.println(postDTOS);

        return ResponseEntity.status(HttpStatus.OK).body(postDTOS);
    }

    // 투표 게시판 게시물 전체 조회
    @GetMapping("/votePost")
    public ResponseEntity<List<PostDTO>> postVoteViewAll(@RequestParam(name = "keyword", required = false) String keyword) {
        Sort sortCondition = Sort.by("postDate").descending(); // 최신 순 정렬

        BooleanBuilder builder = new BooleanBuilder();
        QPost qPost = QPost.post;

        // postType이 "vote"인 것만 조회
        builder.and(qPost.postType.eq("vote"));

        if (keyword != null) {
            BooleanExpression expression = qPost.postDesc.like("%" + keyword + "%");
            builder.and(expression);
        }

        List<Post> posts = postService.postVoteViewAll(builder, sortCondition);

        // 각 게시물에 대한 이미지 URL 추가
        List<PostDTO> postDTOS = new ArrayList<>();
        for (Post post : posts) {
            List<PostImg> postImgs = postImgService.findByPost_PostCode(post.getPostCode());

            PostDTO postDTO = PostDTO.builder()
                    .postCode(post.getPostCode())
                    .postType(post.getPostType())
                    .postDesc(post.getPostDesc())
                    .userCode(post.getUser().getUserCode())
                    .imageUrls(postImgs.stream().map(PostImg::getPostImgUrl).collect(Collectors.toList()))
                    .build();
            postDTOS.add(postDTO);
        }

        return ResponseEntity.ok(postDTOS);
    }

    // 게시물 1개 보기 ( 상세페이지 조회)
    @GetMapping("/post/{postCode}")
    public ResponseEntity<PostDTO> view(@PathVariable(name="postCode") int postCode) {
        Post post = postService.view(postCode);
        int likeCount = likesService.countLikesByPost(post);
        int collectionCount = collectionService.countCollectionByPost(post);
        List<PostImg> postImgs = postImgService.findByPost_PostCode(postCode);
        List<Product> products = productService.certainProduct(postCode);
        List<Integer> tagCodes = postTagService.findPostTag(postCode);
        List<PostImgDTO> imgDTO = new ArrayList<>();
        List<Tag> tags = tagService.findTagByTagCode(tagCodes);

        for(PostImg pi : postImgs) {
            imgDTO.add(new PostImgDTO(pi.getPostImgCode(), pi.getPostImgUrl()));
        }

        PostDTO postDTO = PostDTO.builder()
                .postCode(post.getPostCode())
                .postDesc(post.getPostDesc())
                .postPublicYn(post.getPostPublicYn())
                .postType(post.getPostType())
                .postDate(post.getPostDate())
                .likeCount(likeCount)
                .collectionCount(collectionCount)
                .products(products)
                .tagCodes(tagCodes)
                .tags(tags)
                .userCode(post.getUser().getUserCode())
                .postImgInfo(imgDTO)
                .imageUrls(postImgs.stream().map(PostImg::getPostImgUrl).collect(Collectors.toList()))
                .build();

//        System.out.println(postDTO);
        return ResponseEntity.ok(postDTO);
    }

    // 투표 게시물 조회
    @GetMapping("/votePost/{postCode}")
    public ResponseEntity votePostView(@PathVariable(name="postCode") int postCode){
        Post post = postService.view(postCode); // post 코드로 게시물 조회
        VoteDesc voteDesc = voteDescService.voteDesc(postCode); // 포스트 코드로 투표 게시물 내용 1, 2 조회
        List<PostImg> postImgs = postImgService.findByPost_PostCode(postCode);
        List<PostImgDTO> imgDTO = new ArrayList<>();
        int yCount = voteService.voteCountY(postCode);
        int nCount = voteService.voteCountN(postCode);
        int voteCount = voteService.voteCount(postCode);
        for(PostImg pi : postImgs) {
            imgDTO.add(new PostImgDTO(pi.getPostImgCode(), pi.getPostImgUrl()));
        }

        PostDTO postDTO = PostDTO.builder()
                .postCode(post.getPostCode())
                .postDesc(post.getPostDesc())
                .postPublicYn(post.getPostPublicYn())
                .postType(post.getPostType())
                .postDate(post.getPostDate())
                .userCode(post.getUser().getUserCode())
                .postImgInfo(imgDTO)
                .yCount(yCount)
                .nCount(nCount)
                .voteCount(voteCount)
                .voteTextFirst(voteDesc.getVoteTextFirst())
                .voteTextSecond(voteDesc.getVoteTextSecond())
                .imageUrls(postImgs.stream().map(PostImg::getPostImgUrl).collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(postDTO);
    }

    // 해당 유저가 만든 게시물 조회
    @GetMapping("/{userCode}/post")
    public ResponseEntity<UserPostSummaryDTO> getPostListByUser(@PathVariable("userCode") int userCode){
        UserPostSummaryDTO userPostSummaryDTO = postService.getPostListByUser(userCode);
        return ResponseEntity.status(HttpStatus.OK).body(userPostSummaryDTO);
    }

    @GetMapping("/{userCode}/getUserVote")
    public ResponseEntity<UserPostSummaryDTO> getUserVote(@PathVariable("userCode") int userCode){
        UserPostSummaryDTO userPostSummaryDTO = postService.getVoteListByUser(userCode);
        return ResponseEntity.status(HttpStatus.OK).body(userPostSummaryDTO);
    }

    // 업로드 경로
    private String path = "\\\\192.168.10.51\\flow\\postImg";

    // 게시물 업로드
    @PostMapping("/post")
    public ResponseEntity upload(PostDTO postDTO) throws IOException {

//        System.out.println(postDTO);
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
                    .postDesc("")
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
            File imageFile = new File(path + File.separator + fileName);

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

        System.out.println("태그 : " + tags);
        // PostTag 저장
        if(tags != null && !tags.isEmpty()){
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

    // 투표 게시물 업로드
    @PostMapping("/uploadVote")
    public ResponseEntity uploadVote(PostDTO postDTO) throws IOException {
        List<MultipartFile> files = postDTO.getImageFiles();

        Post post = new Post();

        // post 업로드
        if( postDTO.getPostDesc() != null && !postDTO.getPostDesc().isEmpty()) {
            post = postService.save(Post.builder()
                    .postType("vote")
                    .postPublicYn("Y")
                    .postDesc(postDTO.getPostDesc())
                    .postDate(LocalDateTime.now())
                    .user(userService.findUser(postDTO.getUserCode()))
                    .build());
        } else {
            post = postService.save(Post.builder()
                    .postType("vote")
                    .postPublicYn("Y")
                    .postDate(LocalDateTime.now())
                    .user(userService.findUser(postDTO.getUserCode()))
                    .build());
        }

        VoteDesc voteDesc = new VoteDesc();
        voteDesc.setPost(post);
        if(postDTO.getVoteTextFirst() != null){
            voteDesc.setVoteTextFirst(postDTO.getVoteTextFirst());
        }
        if(postDTO.getVoteTextSecond() != null){
            voteDesc.setVoteTextSecond(postDTO.getVoteTextSecond());
        }
        voteDescService.save(voteDesc);


        // 이미지 DB 저장
        for(MultipartFile f : files) {
            // 파일 업로드
            String uuid = UUID.randomUUID().toString();

            String fileName = uuid + "_" + f.getOriginalFilename();
            //http://192.168.10.51:8082/postImg/파일
            File imageFile = new File(path + File.separator + fileName);

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
        return ResponseEntity.status(HttpStatus.OK).build();
    };

    // 투표 게시물 수정
    @PutMapping("/postVote")
    public ResponseEntity updateVote(@RequestBody PostDTO postDTO){

        int postCode = postDTO.getPostCode();

        Post prePost = postService.view(postCode);

        List<Product> products = postDTO.getProducts();
        List<Integer> tags = postDTO.getTagCodes();

        Post post = postService.save(Post.builder()
                .postCode(postCode)
                .postType("vote")
                .postDesc(postDTO.getPostDesc())
                .user(userService.findUser(postDTO.getUserCode()))
                .build());

        return ResponseEntity.status(HttpStatus.OK).build();
    };

    // 게시물 수정
    @PutMapping("/post")
    public ResponseEntity update(@RequestBody PostDTO postDTO){

        int postCode = postDTO.getPostCode();

        Post prePost = postService.view(postCode);

        List<Product> products = postDTO.getProducts();
        List<Integer> tags = postDTO.getTagCodes();
        
        // post
        Post post = postService.save(Post.builder()
                .postCode(postCode)
                .postType("post")
                .postDesc(postDTO.getPostDesc())
                .postDate(prePost.getPostDate())
                .postPublicYn(postDTO.getPostPublicYn())
                .user(userService.findUser(postDTO.getUserCode()))
                .build());


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
                        // 새로 추가된 제품
                        Product product = productService.addProduct(Product.builder()
                                .productCode(p.getProductCode())
                                .productBrand(p.getProductBrand())
                                .productName(p.getProductName())
                                .productSize(p.getProductSize())
                                .productBuyFrom(p.getProductBuyFrom())
                                .productLink(p.getProductLink())
                                .post(Post.builder()
                                        .postCode(postCode)
                                        .build())
                                .build());
                        //log.info("product : " + product);
                }
            }
        }

        // 기존 tag 삭제
        postTagService.deletePostTagByPostCode(postCode);

        // PostTag 저장
        if(tags != null && !tags.isEmpty()){
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
                    commentReportService.cancelCommentReport(commentReport.getCommentReportCode());
                }
            }
        }

        // 그 글에 있는 댓글들 삭제
        commentDAO.deleteCommentByPostCode(postCode);

        // 좋아요가 되어있을때
        likesDAO.deleteLikesByPostCode(postCode);

        // 글에 있는 사진들 삭제
        List<PostImg> postImgs = postImgService.findByPost_PostCode(postCode);

        for(PostImg pi : postImgs){
            // 이미지파일 삭제
            String url = pi.getPostImgUrl();
            String fileName = url.substring(url.lastIndexOf("\\") +1);
//            System.out.println("파일명 : " +fileName);
            File file = new File(path + "\\" +  fileName);
//            System.out.println("삭제 경로" + file);
            file.delete();
        }

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
