package com.master.flow.controller;

import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.dto.PostImgDTO;
import com.master.flow.model.vo.PostImg;
import com.master.flow.service.PostImgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class PostImgController {


    @Autowired
    private PostImgService service;

    private String path = "\\\\192.168.10.51\\flow\\postImg";


    // 게시물 1개 이미지보기
    @GetMapping("/postImg/{postCode}")
    public ResponseEntity ImgList(@PathVariable(name="postCode") int postCode) {
        List<PostImg> imgs = service.findByPost_PostCode(postCode);
        List<PostImgDTO> imgDTO = new ArrayList<>();

        for(PostImg pi : imgs) {
            imgDTO.add(new PostImgDTO(pi.getPostImgCode(), pi.getPostImgUrl()));
        }

        System.out.println(imgDTO);

        return ResponseEntity.ok(null);
    }

    // 게시물 수정 시 postImgCode로 사진 삭제
    @DeleteMapping("/postImg")
    public ResponseEntity updateImages(@RequestBody List<Integer> images){
        System.out.println(images);

        for (Integer im : images) {
            // 파일 삭제
            PostImg img = service.fetchImg(im);
            String url = img.getPostImgUrl();
            String fileName = url.substring(url.lastIndexOf("\\") +1);
            File file = new File(path + "\\" +  fileName);
            file.delete();

            // DB 삭제
            service.updateImages(im);
        }

        return ResponseEntity.noContent().build();
    }

}
