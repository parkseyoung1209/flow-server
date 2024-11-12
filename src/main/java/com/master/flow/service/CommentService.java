package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import com.master.flow.model.dto.CommentDTO;
import com.master.flow.model.vo.Comment;
import com.master.flow.model.vo.QComment;
import com.master.flow.model.vo.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentService {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private CommentDAO commentDao;

    @Autowired
    private JPAQueryFactory queryFactory;

    private final QComment qComment = QComment.comment;

    // 사용자 정보 가져오는 메서드
    private User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null && auth.isAuthenticated()) {
            return (User) auth.getPrincipal();
        }
        return null;
    }

//    // 댓글 사진 첨부
//    public String uploadImg(MultipartFile file) throws IOException {
//        // 저장 경로
//        String filePath = uploadPath + File.separator + file.getOriginalFilename();
//        File destinationFile = new File(filePath);
//
//        // 파일 저장
//        file.transferTo(destinationFile);
//
//        // 파일 URL 반환
//        return "http://192.168.10.51:8081/comment" + file.getOriginalFilename();
//    }

    // 댓글 조회 - 상위 댓글 조회
    public List<Comment> getAllComment(int postCode) {
        return queryFactory
                .selectFrom(qComment)
                .where(qComment.post.postCode.eq(postCode))
                .where(qComment.parentCommentCode.eq(0))
                .orderBy(qComment.commentDate.asc())
                .fetch();
    }

    // 대댓글 조회
    public List<Comment> getParentCommentCode(int parentCommentCode) {
        return queryFactory
                .selectFrom(qComment)
                .where(qComment.parentCommentCode.eq(parentCommentCode))
                .orderBy(qComment.commentDate.asc())
                .fetch();
    }

    // 댓글 작성
    public void addComment(CommentDTO dto) {
        log.info("parent : " + dto.getParentCommentCode());
        commentDao.saveComment(dto.getCommentDesc(), dto.getPostCode(), dto.getUserCode(), dto.getParentCommentCode());
    }

    // 댓글 수정
    public void updateComment(CommentDTO dto) {
        Optional<Comment> optionalComment = commentDao.findById(dto.getCommentCode());
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setCommentDesc(dto.getCommentDesc());
            commentDao.save(comment);
        } else {
            throw new EntityNotFoundException("Comment not found with code: " + dto.getCommentCode());
        }
    }

    // 댓글 삭제
    public void deleteComment(int commentCode) {

        // 자식 댓글 조회
        List<Comment> childComments = queryFactory.selectFrom(qComment)
                .where(qComment.parentCommentCode.eq(commentCode)).fetch();
        int childCount = childComments.size();

        // 자식 댓글 삭제
        for (Comment childComment : childComments) {
            commentDao.deleteById(childComment.getCommentCode());
        }

        // 부모 댓글 삭제
        commentDao.deleteById(commentCode);

        // 해당 댓글에 부모 댓글이 있는지 체크
//        deleteParent(comment.getCommentCode());
    }

    // 대댓글 삭제
    public void deleteParent(int parentCommentCode) {
        if(parentCommentCode > 0) {
            // 부모 댓글의 자식 댓글이 모두 삭제되었는지 체크
            List<Comment> parents = queryFactory.selectFrom(qComment)
                    .where(qComment.parentCommentCode.eq(parentCommentCode)).fetch();
            int parentCount = parents.size();
            if(parentCount == 0) {
                Comment parent = commentDao.findById(parentCommentCode).get();
                if(parent.getCommentDelYn() != null) {
                    commentDao.deleteById(parent.getCommentCode());

                    deleteParent(parent.getParentCommentCode());
                }
            }
        }
    }

    // 댓글 신고
    public void reportComment(int commentCode, String reportDesc) {
        Comment comment = commentDao.findById(commentCode).get();
    }
}