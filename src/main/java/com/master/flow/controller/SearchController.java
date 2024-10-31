package com.master.flow.controller;

import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.dto.PostInfoDTO;
import com.master.flow.model.dto.SearchDTO;
import com.master.flow.model.vo.PostImg;
import com.master.flow.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*", maxAge = 6000)
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/posts")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestBody SearchDTO searchDTO) {

        List<PostInfoDTO> postInfoList = searchService.searchPosts(searchDTO);

        List<PostDTO> postDTOS = postInfoList.stream().map(postInfo -> {
            return PostDTO.builder()
                    .postCode(postInfo.getPost().getPostCode())
                    .postDesc(postInfo.getPost().getPostDesc())
                    .userCode(postInfo.getPost().getUser().getUserCode())
                    .user(postInfo.getPost().getUser())
                    .imageUrls(postInfo.getImageFiles().stream().map(PostImg::getPostImgUrl).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(postDTOS);
    }
}
