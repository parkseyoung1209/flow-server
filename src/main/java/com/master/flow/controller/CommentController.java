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




    // 댓글 조회
    @GetMapping("/{postCode}/comment")
    public ResponseEntity getComments(@PathVariable(name = "postCode") int postCode) {
        List<Comment> comments = commentService.getAllComment(postCode);
        List<CommentDTO> response = new ArrayList<>();

        for(Comment comment : comments) {
            CommentDTO result = CommentDTO.builder()
                    .commentCode(comment.getCommentCode())
                    .commentDate(comment.getCommentDate())
                    .commentDesc(comment.getCommentDesc())
                    .commentDelYn(comment.getCommentDelYn())
                    .postCode(comment.getPost().getPostCode())
                    .commentImgUrl(comment.getUserCode().getUserProfileUrl())
                    .userCode(comment.getUserCode().getUserCode())
                    .userNickname(comment.getUserCode().getUserNickname())
                    .build();
            List<Comment> replies = commentService.getParentCommentCode(comment.getCommentCode());
            result.setReplies(replies);
            response.add(result);
        }

        System.err.println(comments);
        return ResponseEntity.ok(response);
    }

    // 댓글 작성
    @PostMapping("/addcomment")
    public ResponseEntity addComment(@RequestBody CommentDTO dto) {
        commentService.addComment(dto);
//        return ResponseEntity.ok(commentService.addComment(vo));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 댓글 수정
    @PutMapping("/updatecomment")
    public ResponseEntity updateComment(@RequestBody CommentDTO dto) {
        commentService.updateComment(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 댓글 삭제
    @DeleteMapping("/deletecomment/{commentCode}")
    public ResponseEntity deleteComment(@PathVariable(name="commentCode") int commentCode) {
        commentService.deleteComment(commentCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

//    // 대댓글 삭제
//    @DeleteMapping("/deleteparent/{parentCommentCode}")
//    public ResponseEntity<Void> deleteParent(@PathVariable(name="parentCommentCode") int parentCommentCode) {
//        commentService.deleteParent(parentCommentCode);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
}