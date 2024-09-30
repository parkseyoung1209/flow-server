package com.master.flow.controller;

import com.master.flow.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class PostController {
    @Autowired
    private PostService postService;

    @DeleteMapping("/delPost")
    public ResponseEntity delPost(int postCode) {
        postService.delPost(postCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
