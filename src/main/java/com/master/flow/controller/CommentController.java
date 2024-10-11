package com.master.flow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.master.flow.model.dto.CommentDTO;
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
import java.util.ArrayList;
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

    // 한 게시물에 따른 모든 댓글 조회
    @GetMapping("/{postCode}/comment")
    public ResponseEntity comments(@PathVariable(name = "postCode") int postCode) {
        List<Comment> comments = commentService.getAllComment(postCode);
        List<CommentDTO> response = commentList(comments);
        return ResponseEntity.ok(comments);
    }

    // 무한 댓글 추가
    public List<CommentDTO> commentList(List<Comment> comments) {
        List<CommentDTO> response = new ArrayList<>();

        for (Comment comment : comments) {
            List<Comment> replies = commentService.getAllComment(comment.getCommentCode());
            List<CommentDTO> repliesDTO = new ArrayList<>();
            CommentDTO dto = new CommentDTO();
            dto.setReplies(repliesDTO);
            response.add(dto);
        }
        return response;
    }

    // 대댓글 작성
    public CommentDTO parentCommentCode(Comment comment) {
        return CommentDTO.builder()
                .commentCode(comment.getCommentCode())
                .commentDesc(comment.getCommentDesc())
                .commentImgUrl(comment.getCommentImgUrl())
                .commentDate(comment.getCommentDate())
                .commentDelYn(comment.getCommentDelYn())
                .postCode(comment.getPostCode())
                .user(comment.getUser())
                .build();
    }

    // 댓글 수정
    @PutMapping("/updatecomment/{commentCode}")
    public ResponseEntity update(@RequestBody Comment vo) {
        commentService.updateReply(vo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 대댓글 수정
    @PutMapping("/updatereply/{commentCode}")
    public ResponseEntity<Comment> updateReply(@PathVariable int commentCode, @RequestBody Comment updateReply) {
        return ResponseEntity.of(commentService.updateReply(commentCode, updateReply));
    }

    // 댓글 삭제
    @DeleteMapping("/deletecomment/{commentCode}")
    public ResponseEntity delete(@PathVariable(name="commentCode") int commentCode) {
        commentService.deleteComment(commentCode);
        return ResponseEntity.status(HttpStatus.OK).build();
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
