package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import com.master.flow.model.dao.CommentReportDAO;

import com.master.flow.model.vo.Comment;
import com.master.flow.model.vo.CommentReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CommentReportService {

    @Autowired
    private CommentDAO commentDAO;
    
    @Autowired
    private CommentReportDAO commentReportDao;

    @Autowired
    private CommentService commentService;

//    신고한 댓글 전부 보여주기
    public List<CommentReport> showAllCommentReport() {return commentReportDao.findAll();}

//    신고한 댓글 한개 삭제하기
    public void delCommentReport(int commentReportCode) {
        Comment comment = commentDAO.findById(commentReportDao.findById(commentReportCode).get().getComment().getCommentCode()).get();

        commentReportDao.deleteCommentReportByCommentCode(comment.getCommentCode());

        if(comment.getParentCommentCode() == 0) {
            commentService.deleteComment(comment.getCommentCode());
        } else if(comment.getParentCommentCode() > 0) {
            commentService.deleteParent(comment.getParentCommentCode());
        }
    }
//    신고된 댓글 취소하기
    public void cancelCommentReport(int commentReportCode) {
        commentReportDao.deleteById(commentReportCode);
    }
}
