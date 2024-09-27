package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import com.master.flow.model.vo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    /* 댓글 작성 */
    @Autowired
    private CommentDAO commentDAO;

    public Comment saveComment(Comment comment) {
        return commentDAO.save(comment);
    }
}
