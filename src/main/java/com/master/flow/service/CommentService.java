package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import com.master.flow.model.vo.Comment;
import com.master.flow.model.vo.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public CommentService(CommentDAO commentDAO) {
        this.commentDao = commentDAO;
    }

    // 댓글 작성
    public Comment create(Comment vo) {
        return commentDao.save(vo);
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
    public List<Comment> createReply(int parentCommentCode) {
        return queryFactory
                .selectFrom(qComment)
                .where(qComment.parentCommentCode.eq(parentCommentCode))
                .orderBy(qComment.commentDate.asc())
                .fetch();
    }

    // 댓글 수정
    public void updateReply(Comment vo) {
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
        commentDao.deleteById(commentCode);
    }

    // 대댓글 삭제
    public void deleteReply(int commentCode) {
        commentDao.deleteById(commentCode);
    }

    // 사진 첨부 : 파일 업로드
    private String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        File dest = new File(uploadPath + File.separator + fileName);
        file.transferTo(dest);
        return fileName;
    }

    // 댓글 신고
    public void reportComment(int commentCode, String reportDesc) {
        Comment comment = commentDao.findById(commentCode).get();
    }
}











