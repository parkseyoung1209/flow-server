package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDao;

//    댓글 한개 삭제(대댓글 미고려)
    public void deleteComment(int commentId) {
        commentDao.deleteById(commentId);
    }
}
