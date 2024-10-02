package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import com.master.flow.model.vo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    /* 댓글 작성 */
    @Autowired
    private CommentDAO commentDao;

    public Comment saveComment(Comment comment) {
        return commentDao.save(comment);

//    댓글 한개 삭제(대댓글 미고려)
    public void deleteComment(int commentId) {
        commentDao.deleteById(commentId);
    }
}
