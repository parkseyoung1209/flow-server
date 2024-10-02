package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import com.master.flow.model.vo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class CommentService {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private CommentDAO commentDao;

    public CommentService(CommentDAO commentDAO) {
        this.commentDao = commentDAO;
    }

    // 댓글 저장
    public Comment saveComment(Comment comment) {
        return commentDao.save(comment);
    }

    // 댓글 작성 & 사진 첨부
    public Comment addComment(Comment comment, MultipartFile file) throws IOException {
        if(file != null && !file.isEmpty()) {
            String fileName = uploadFile(file);
            comment.setCommentImgUrl(fileName);
        }
        return commentDao.save(comment);
    }

    // 댓글 사진 첨부
    private String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        File dest = new File(uploadPath + File.separator + fileName);
        file.transferTo(dest);
        return fileName;
    }

    // 대댓글 작성
    public Comment addReply(int parentCommentCode, Comment reply) {
        Comment parentComment = commentDao.findById(parentCommentCode).orElseThrow(() -> new RuntimeException("Parent comment not found"));
        reply.setParentComment(parentComment);
        return commentDao.save(reply);
    }

    // 댓글 한개 삭제
    public void deleteComment(int commentId) {
        commentDao.deleteById(commentId);
    }

    // 모든 댓글 조회
    public List<Comment> getAllComment() {
        return commentDao.findAll();
    }

    // 댓글 신고
    public void reportComment(int commentCode, String reportDesc) {
        Comment comment = commentDao.findById(commentCode).get();
    }
}
