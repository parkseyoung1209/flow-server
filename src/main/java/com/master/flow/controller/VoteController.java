package com.master.flow.controller;

import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.Vote;
import com.master.flow.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class VoteController {

    @Autowired
    private VoteService voteService;

    @GetMapping("/vote")
    public ResponseEntity postVoteViewAll(Post vo){
        // 추후 "vote" type 만 조회 하도록 변경
        return ResponseEntity.status(HttpStatus.OK).body(voteService.postVoteViewAll(vo));
    }

    // 내가 한 투표 변경 (찬성 -> 반대 or 반대 -> 찬성)
    @PutMapping("/vote")
    public ResponseEntity changeVote (@RequestBody Vote vo){
        voteService.changVote(vo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
