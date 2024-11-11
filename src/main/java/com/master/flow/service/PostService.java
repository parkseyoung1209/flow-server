package com.master.flow.service;

import com.master.flow.model.dao.*;
import com.master.flow.model.dto.PostInfoDTO;
import com.master.flow.model.dto.UserPostSummaryDTO;
import com.master.flow.model.vo.Comment;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import com.master.flow.model.vo.User;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    @Autowired
    private PostDAO postDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LikesDAO likesDAO;
    @Autowired
    private CollectionDAO collectionDAO;
    @Autowired
    private PostImgDAO postImgDao;
    @Autowired
    private ProductDAO productDao;
    @Autowired
    private CommentDAO commentDAO;
    @Autowired
    private PostReportDAO postReportDAO;
    @Autowired
    private PostTagDAO postTagDAO;
    @Autowired
    private VoteDAO voteDAO;

    // 서비스 계층에서 페이징 처리
    public Page<Post> viewAll(BooleanBuilder builder, Pageable pageable) {
        return postDAO.findAll(builder, pageable);
    }

    // 게시물 1개 보기 ( 상세페이지 조회)
    public Post view(int postCode) {
        return postDAO.findById(postCode).get();
    }

    // 투표 게시물 전체 조회
    public List<Post> postVoteViewAll(BooleanBuilder builder, Sort sort) {
        return postDAO.findByPostTypesVote();
    }

    // 투표 게시물 1개 조회
    public Post votePostView(int postCode) {
        return postDAO.findByPostTypesVote().get(postCode);
    }

    // 게시물 업로드&수정
    public Post save(Post post) {
        // save : postCode(primary key)가 없으면 추가/ id가 있으면 수정으로 사용
        return postDAO.save(post);
    }

    public void delPost(int postCode){
        postDAO.deleteById(postCode);
    }

    // 유저 코드로 게시물 조회
    public UserPostSummaryDTO getPostListByUser (int userCode){
        Optional<User> user = userDAO.findById(userCode);

        List<Post> post = postDAO.findByUser_UserCode(userCode);

        List<PostInfoDTO> postInfoList = post.stream().map(posts -> {
            List<PostImg> postImgs = postImgDao.findByPost_PostCode(posts.getPostCode());
            int likeCount = likesDAO.countByPost(posts);
            int collectionCount = collectionDAO.countByPost(posts);

            return new PostInfoDTO(posts, likeCount, collectionCount, postImgs);
        }).collect(Collectors.toList());

        int totalSavedPost = postInfoList.size();

        return new UserPostSummaryDTO(postInfoList, totalSavedPost);

    }
    // 유저가 만든 투표 조회
    public UserPostSummaryDTO getVoteListByUser (int userCode){
        Optional<User> user = userDAO.findById(userCode);

        List<Post> post = postDAO.findByUser_UserVote(userCode);

        List<PostInfoDTO> postInfoList = post.stream().map(posts -> {
            List<PostImg> postImgs = postImgDao.findByPost_PostCode(posts.getPostCode());
            int likeCount = likesDAO.countByPost(posts);
            int collectionCount = collectionDAO.countByPost(posts);

            return new PostInfoDTO(posts, likeCount, collectionCount, postImgs);
        }).collect(Collectors.toList());

        int totalSavedPost = postInfoList.size();

        return new UserPostSummaryDTO(postInfoList, totalSavedPost);

    }

    public List<Comment> delPostReportAndComment(int postCode){
        // 이미 글이 신고되어있는 상태일때
        postReportDAO.deletePostReportByPostCode(postCode);
        // 그 글에 있는 댓글들 중 신고된 댓글
        return commentDAO.findByPostCode(postCode);
    }

    public void delCommentAndLikes(int postCode){
        // 그 글에 있는 댓글들 삭제
        commentDAO.deleteCommentByPostCode(postCode);
        // 좋아요가 되어있을때
        likesDAO.deleteLikesByPostCode(postCode);
    }

    public void delPostOther(int postCode){
        postImgDao.deletePostImgByPostCode(postCode);
        // 글에 등록된 상품들 삭제
        productDao.deleteProductByPostCode(postCode);
        // 그 글이 유저가 저장한 글일 경우
        collectionDAO.deleteCollectionByPostCode(postCode);
        // 글에 적용된 태그들 삭제
        postTagDAO.deletePostTagByPostCode(postCode);
        // 투표 글인 경우
        voteDAO.deleteVoteByPostCode(postCode);
    }

}

