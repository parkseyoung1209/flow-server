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

    // 투표
    @PostMapping("/postVote/vote")
    public ResponseEntity vote (@RequestBody Vote vo){
        voteService.vote(vo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 투표 취소
    @PostMapping("/postVote/vote/{voteCode}")
    public ResponseEntity removeVote (@PathVariable(name="voteCode") int userCode){
        voteService.removeVote(userCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

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

    // 반대 투표 수
    @GetMapping("/postVote/{postCode}/countN")
    public ResponseEntity voteCountN (@PathVariable(name="postCode") int voteCode){
        return ResponseEntity.ok(voteService.voteCountN(voteCode));
    }

    // 로그인한 사람의 투표 체크 여부
    @GetMapping("/postVote/{postCode}/check")
    public ResponseEntity check (@PathVariable(name = "postCode") int voteCode){
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
