package com.master.flow.controller;

import com.master.flow.service.PostReportService;
import com.master.flow.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class PostReportController {
    @Autowired
    private PostReportService postReportService;
    @Autowired
    private PostService postService;

    //    신고한 글 전부 보여주기
    @GetMapping("/showAllPostReport")
    public ResponseEntity showAllPostReport() {
        return ResponseEntity.status(HttpStatus.OK).body(postReportService.showAllPostReport());
    }

    @DeleteMapping("/delPostReport")
    public ResponseEntity delPostReport(int postReportCode) {
        postService.delPost(postReportService.delPostReport(postReportCode));

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
