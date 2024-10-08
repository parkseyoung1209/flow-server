package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import com.master.flow.model.vo.Comment;
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

    public CommentService(CommentDAO commentDAO) {
        this.commentDao = commentDAO;
    }

    // 모든 댓글 조회
    public List<Comment> getAllComment() {
        return commentDao.findAll();
    }

    // 댓글 & 대댓글 저장, 작성, 사진 첨부
    public Comment saveComment(Integer parentCommentCode, Comment comment, MultipartFile file) throws IOException {
        if(file != null && !file.isEmpty()) {
            String fileName = uploadFile(file);
            comment.setCommentImgUrl(fileName);
        }

        // 대댓글
        if(parentCommentCode != null && parentCommentCode > 0) {
            Comment parentComment = commentDao.findById(parentCommentCode)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }
        return commentDao.save(comment);
    }

    // 사진 첨부 : 파일 업로드
    private String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        File dest = new File(uploadPath + File.separator + fileName);
        file.transferTo(dest);
        return fileName;
    }
    
    // 댓글 수정
    public Comment updateComment(int commentId, Comment updatedComment) {
        Comment existingComment = commentDao.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        existingComment.setCommentDesc(updatedComment.getCommentDesc());
        existingComment.setCommentImgUrl(updatedComment.getCommentImgUrl());
        existingComment.setCommentDelYn(updatedComment.getCommentDelYn());
        return commentDao.save(existingComment);
    }

    // 대댓글 수정
    public Comment updateReply(int replyId, Comment updatedReply) {
        Comment existingReply = commentDao.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));
        existingReply.setCommentDesc(updatedReply.getCommentDesc());
        existingReply.setCommentImgUrl(updatedReply.getCommentImgUrl());
        existingReply.setCommentDelYn(updatedReply.getCommentDelYn());

        return commentDao.save(existingReply);
    }

    // 댓글 & 대댓글 삭제
    public void deleteComment(int commentId) {
        commentDao.deleteById(commentId);
    }

    // 댓글 신고
    public void reportComment(int commentCode, String reportDesc) {
        Comment comment = commentDao.findById(commentCode).get();
    }
}











