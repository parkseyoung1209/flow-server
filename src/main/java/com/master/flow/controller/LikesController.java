package com.master.flow.controller;

import com.master.flow.service.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class LikesController {
    @Autowired
    private LikesService likesService;

//    좋아요 한개 삭제
    @DeleteMapping("/delLike")
    public ResponseEntity delLike(@RequestParam("postCode") int postCode) {
        likesService.delLike(postCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
