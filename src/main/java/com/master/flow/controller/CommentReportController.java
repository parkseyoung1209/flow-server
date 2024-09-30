package com.master.flow.controller;

import com.master.flow.service.CommentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class CommentReportController {
    @Autowired
    private CommentReportService commentReportService;
    
//    신고한 댓글 전부 보여주기
    @GetMapping("/showAllCommentReport")
    public ResponseEntity showAllCommentReport() {
        return ResponseEntity.status(HttpStatus.OK).body(commentReportService.showAllCommentReport());
    }

//    신고한 댓글 한개 삭제하기
    @DeleteMapping("/delCommentReport")
    public ResponseEntity delCommentReport(@RequestParam int commentReportCode) {
        commentReportService.delCommentReport(commentReportCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
