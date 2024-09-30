package com.master.flow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.master.flow.model.vo.Comment;
import com.master.flow.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class CommentController {


    @Autowired
    private CommentService commentService;

    // 댓글 등록 API & 사진 첨부
    @PostMapping("/{id}")
    public ResponseEntity<Comment> addComment(@RequestParam("file")MultipartFile file, @RequestParam("comment") String commentJson) throws IOException {
        Comment comment = new ObjectMapper().readValue(commentJson, Comment.class);
        Comment savedComment = commentService.addComment(comment, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    // 모든 댓글 조회 API
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComment() {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllComment());
    }

    // 댓글 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable int id) {
        commentService.deleteComment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 댓글 신고
    @PostMapping("/{id}/report")
    public ResponseEntity<Void> reportComment(@PathVariable int id, @RequestBody String reportDESC) {
        commentService.reportComment(id, reportDESC);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
