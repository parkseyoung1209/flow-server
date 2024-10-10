package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import com.master.flow.model.vo.Comment;
import com.master.flow.model.vo.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    public CommentService(CommentDAO commentDAO) {
        this.commentDao = commentDAO;
    }

    // 댓글 작성
    public Comment create(Comment vo) {
        return commentDao.save(vo);
    }

    // 대댓글 작성
    public Comment createReply(Comment vo) {
        vo.setParentCommentCode(vo.getParentCommentCode());
        return commentDao.save(vo);
    }
    
    // 댓글 수정
    public Optional<Comment> update(int commentCode, Comment updateComment) {
        return commentDao.findById(commentCode).map(comment -> {
            comment.setCommentDesc(updateComment.getCommentDesc());
            comment.setCommentImgUrl(updateComment.getCommentImgUrl());
            return commentDao.save(comment);
        });
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

    // 사진 첨부 : 파일 업로드
    private String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        File dest = new File(uploadPath + File.separator + fileName);
        file.transferTo(dest);
        return fileName;
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
        commentDao.deleteById(commentCode);
    }

    // 대댓글 삭제
    public void deleteReply(int commentCode) {
        commentDao.deleteById(commentCode);
    }

    // 댓글 신고
    public void reportComment(int commentCode, String reportDesc) {
        Comment comment = commentDao.findById(commentCode).get();
    }
}











