package com.master.flow.controller;

import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import com.master.flow.model.vo.Product;
import com.master.flow.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class PostController {

    @Autowired
    private PostService postService;

    /*
    *  전송하는 방법이..!
    * 자바스크립트에서 보내는 방법!
    * 그때 파일을 같이 보내야 할 때는 FormData 객체 생성해서
    * 각각의 값들 append로 추가해서 마지막에 보내기만 하면 끝!
    * */

    // 게시물 업로드
    @PostMapping("/post")
    public ResponseEntity upload(@RequestBody PostDTO postDto){
        // user 임의 설정, img 추가 필요
        
        //System.out.println(postDto)
        postService.change(postDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    };

    // 게시물 수정
    @PutMapping("/post")
    public ResponseEntity update(@RequestBody PostDTO postDto){
        postService.change(postDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    };

}
