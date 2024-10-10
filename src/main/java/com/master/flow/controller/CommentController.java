package com.master.flow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.master.flow.model.vo.Comment;
import com.master.flow.model.vo.User;
import com.master.flow.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // 댓글 작성
    @PostMapping("/addcomment")
    public ResponseEntity add(@RequestBody Comment vo) {

        // 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        vo.setUser(user); // 작성자 정보 설정
        return ResponseEntity.ok(commentService.create(vo));
    }

    // 대댓글 작성
    @PostMapping("/addreply")
    public ResponseEntity<Comment> addReply(@RequestBody Comment vo) {
        
        // 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        vo.setUser(user); // 작성자 정보 설정
        return ResponseEntity.ok(commentService.createReply(vo));
    }

    // 모든 댓글 조회
    @GetMapping("/{postCode}/comment")
    public ResponseEntity comments(@PathVariable(name = "postCode") int postCode) {
        List<Comment> comments = commentService.getAllComment(postCode);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정
    @PutMapping("/updatecomment/{commentCode}")
    public ResponseEntity<Comment> update(@PathVariable int commentCode, @RequestBody Comment updateComment) {
        return ResponseEntity.of(commentService.update(commentCode, updateComment));
    }

    // 대댓글 수정
    @PutMapping("/updatereply/{commentCode}")
    public ResponseEntity<Comment> updateReply(@PathVariable int commentCode, @RequestBody Comment updateReply) {
        return ResponseEntity.of(commentService.updateReply(commentCode, updateReply));
    }

    // 댓글 삭제
    @DeleteMapping("/deletecomment/{commentCode}")
    public ResponseEntity<Void> delete(@PathVariable int commentCode) {
        commentService.deleteComment(commentCode);
        return ResponseEntity.noContent().build();
    }

    // 대댓글 삭제
    @DeleteMapping("/deletereply/{commentCode}")
    public ResponseEntity<Void> deleteReply(@PathVariable int commentCode) {
        commentService.deleteReply(commentCode);
        return ResponseEntity.noContent().build();
    }

    // 댓글 신고
    @PostMapping("/{id}/report")
    public ResponseEntity<Void> reportComment(@PathVariable int id, @RequestBody String reportDESC) {
        commentService.reportComment(id, reportDESC);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
