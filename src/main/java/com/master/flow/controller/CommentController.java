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

    // 모든 댓글 조회
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComment() {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllComment());
    }

    // 댓글 & 대댓긋 작성, 사진 첨부
    public ResponseEntity<Comment> addComment(
            @PathVariable int parentCommentCode,
            @RequestParam("file") MultipartFile file,
            @RequestParam("comment") String commentJson) throws IOException {

        Comment comment = new ObjectMapper().readValue(commentJson, Comment.class);
        Comment savedComment = commentService.saveComment(parentCommentCode, comment, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    // 댓글 & 대댓글 삭제
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
