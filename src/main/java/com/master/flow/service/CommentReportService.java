package com.master.flow.service;

import com.master.flow.model.dao.CommentReportDAO;
import com.master.flow.model.vo.CommentReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentReportService {
    @Autowired
    private CommentReportDAO commentReportDao;

//    신고한 댓글 전부 보여주기
    public List<CommentReport> showAllCommentReport() {return commentReportDao.findAll();}

//    신고한 댓글 한개 삭제하기
    public void delCommentReport(int commentReportCode) {
        commentReportDao.deleteById(commentReportCode);
    }
}
