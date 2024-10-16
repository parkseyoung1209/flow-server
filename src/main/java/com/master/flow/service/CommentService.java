package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import com.master.flow.model.vo.Comment;
import com.master.flow.model.vo.QComment;
import com.master.flow.model.vo.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    // 댓글 작성
    public Comment addComment(Comment vo) {
        System.out.println(vo);
//        User user = getUser();
        if(getUser() != null) {
//            vo.setUserCode(getUser().getUserCode());
            return commentDao.save(vo);
//            return commentDao.saveComment(vo.getCommentDesc(), vo.getPostCode(), getUser().getUserCode(), vo.getParentCommentCode());
        }
        return null;
    }

    // 댓글 사진 첨부
    public String uploadImg(MultipartFile file) throws IOException {
        // 저장 경로
        String filePath = uploadPath + File.separator + file.getOriginalFilename();
        File destinationFile = new File(filePath);

        // 파일 저장
        file.transferTo(destinationFile);

        // 파일 URL 반환
        return "http://192.168.10.51:8081/comment" + file.getOriginalFilename();
    }

    // 상위 댓글 조회
    public List<Comment> getAllComment(int postCode) {
        return queryFactory
                .selectFrom(qComment)
                .where(qComment.postCode.eq(postCode))
                .where(qComment.parentCommentCode.eq(0))
                .orderBy(qComment.commentDate.desc())
                .fetch();
    }

    // 대댓글 작성
    public List<Comment> addParentCommentCode(int parentCommentCode) {
        return queryFactory
                .selectFrom(qComment)
                .where(qComment.parentCommentCode.eq(parentCommentCode))
                .orderBy(qComment.commentDate.asc())
                .fetch();
    }

    // 댓글 수정
    public void updateComment(Comment vo) {
        Comment comment = commentDao.findById(vo.getCommentCode()).get();
        comment.setCommentDesc(vo.getCommentDesc());
        commentDao.save(vo);
    }

    // 대댓글 수정
    public Optional<Comment> updateReply(int commentCode, Comment updateReply) {
        return commentDao.findById(commentCode).map(reply -> {
            reply.setCommentDesc(updateReply.getCommentDesc());
            reply.setCommentImgUrl(updateReply.getCommentImgUrl());
            return commentDao.save(reply);
        });
    }

    // 댓글 삭제
    public void deleteComment(int commentCode) {

        // 자식 댓글이 있는지 체크
        List<Comment> comments = queryFactory.selectFrom(qComment)
                .where(qComment.parentCommentCode.eq(commentCode)).fetch();
        int childCount = comments.size();

        Comment comment = commentDao.findById(commentCode).get();

        if(childCount > 0) {
            commentDao.save(comment);
        } else {
            commentDao.deleteById(commentCode);
        }
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