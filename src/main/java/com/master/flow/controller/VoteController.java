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

    // 전체 투표 현황 조회
    @GetMapping("/postVote/{postCode}/count")
    public ResponseEntity voteCount (@PathVariable(name="postCode") int voteCode) {
        return ResponseEntity.ok(voteService.voteCount(voteCode));
    }

    // 찬성 투표 수
    @GetMapping("/postVote/{postCode}/countY")
    public ResponseEntity voteCountY (@PathVariable(name="postCode") int voteCode) {
        return ResponseEntity.ok(voteService.voteCountY(voteCode));
    }

    // 내가 한 투표 변경 (찬성 -> 반대 or 반대 -> 찬성)
//    @PutMapping("/vote")
//    public ResponseEntity changeVote (@RequestBody Vote vo){
//        voteService.changVote(vo);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
}
