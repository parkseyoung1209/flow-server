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


    @Autowired
    private CommentDAO commentDAO;

    @Value("${upload.path}")
    private String uploadPath;

    public CommentService(CommentDAO commentDAO) {
        this.commentDAO = commentDAO;
    }

    // 댓글 작성 & 사진 첨부
    public Comment addComment(Comment comment, MultipartFile file) throws IOException {
        if(file != null && !file.isEmpty()) {
            String fileName = uploadFile(file);
            comment.setCommentImgUrl(fileName);
        }
        return commentDAO.save(comment);
    }

    // 댓글 사진 첨부
    private String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        File dest = new File(uploadPath + File.separator + fileName);
        file.transferTo(dest);
        return fileName;
    }

    // 모든 댓글 조회
    public List<Comment> getAllComment() {
        return commentDAO.findAll();
    }

    // 댓글 삭제
    public void deleteComment(int commentCode) {
        commentDAO.deleteById(commentCode);
    }

    // 댓글 신고
    public void reportComment(int commentCode, String reprotDesc) {
        Comment comment = commentDAO.findById(commentCode).get();
    }

}
