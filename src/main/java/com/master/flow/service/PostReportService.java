package com.master.flow.service;

import com.master.flow.model.dao.*;
import com.master.flow.model.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PostReportService {
    @Autowired
    private PostReportDAO postReportDao;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentDAO commentDao;

    @Autowired
    private CommentReportService commentReportService;

    @Autowired
    private LikesDAO likesDao;
    @Autowired
    private LikesService likesService;
    @Autowired
    private PostDAO postDAO;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private PostImgDAO postImgDAO;

    @Autowired
    private CollectionDAO collectionDAO;

    @Autowired
    private PostTagDAO postTagDAO;

    @Autowired
    private VoteDAO voteDAO;

    /*
    * 신고한 글 강제 삭제하려면 먼저 삭제해야하는 테이블
    * 1. comment - 댓글들
    * 2. comment_report - 삭제한 댓글과 관련된 신고된 댓글
    * 3. likes - 좋아요
    * 4. post_img - 사진들
    * 5. product - 글에 포함된 제품들
    * 6. post_report - 신고된 글
    * 7. collection - 저장된 글
    * 8. post_tag - 글에 걸려있는 태그들
    * 9. vote - 투표
    * ==> 10. post - 글
    * 9개 삭제해야 글 삭제 가능
    * */

    //    신고한 글 전부 보여주기
    public List<PostReport> showAllPostReport() {return postReportDao.findAll();}

//    신고한 글 한개 선택해서 삭제하기
    public void delPostReport(int postReportCode) {
//        신고한 글 객체
        int postCode = postReportDao.findById(postReportCode).get().getPost().getPostCode();

//        신고한 글에 있는 모든 댓글들
        List<Comment> comments = commentDao.findByPostCode(postCode);
        List<CommentReport> commentsReport = commentReportService.showAllCommentReport();

//        신고하려는 글에 있는 좋아요 테이블 조회 및 삭제
        List<Likes> likesList = likesDao.findByPostCode(postCode);
        for(Likes like : likesList) {
            likesService.delLike(like.getLikesCode());
        }

//        신고한 글에 있는 댓글들 삭제
        for(Comment comment : comments) {
            for(CommentReport commentReport : commentsReport) {
                if(commentReport.getComment().getCommentCode() == comment.getCommentCode()) {
                    commentReportService.delCommentReport(commentReport.getCommentReportCode());
                }
            }
            commentService.deleteComment(comment.getCommentCode());
        }

//        신고한 글 내역 삭제
        postReportDao.deletePostReportByPostCode(postCode);

//      product 테이블 삭제
        productDAO.deleteProductByPostCode(postCode);

//        log.info("code : " + postCode);

//        postImg 테이블 삭제
        postImgDAO.deletePostImgByPostCode(postCode);

//        collection 테이블 삭제
        collectionDAO.deleteCollectionByPostCode(postCode);

//        post_tag 테이블 삭제
        postTagDAO.deletePostTagByPostCode(postCode);

//        vote 테이블 삭제
        voteDAO.deleteVoteByPostCode(postCode);

//        post 테이블 삭제
        postDAO.deleteById(postCode);
    }

    public void reportPost(PostReport vo, User user, int postCode) {
        Post post = postDAO.findById(postCode).get();

        vo.setUser(user);
        vo.setPost(post);

        postReportDao.save(vo);
    }
}
