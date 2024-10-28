package com.master.flow.controller;

import com.master.flow.model.dto.SearchDTO;
import com.master.flow.model.vo.Post;
import com.master.flow.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*", maxAge = 6000)
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/posts")
    public ResponseEntity<List<Post>> searchPosts(@RequestBody SearchDTO searchDTO) {
        List<Post> posts = searchService.searchPosts(searchDTO);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }
}
