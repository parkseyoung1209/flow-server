package com.master.flow.controller;

import com.master.flow.model.vo.PostImg;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class PostImgController {

    private String path = "\\\\192.168.10.51\\flow\\PostImg\\";

    // 공용 폴더 + 데이터베이스 사진 추가
    @ResponseBody
    @PostMapping("/imgUpload")
    public void imgUpload(List<MultipartFile> files, String bsCode) throws IllegalStateException, IOException {
        PostImg postImg;

        int code = Integer.parseInt(bsCode);
        for(MultipartFile f : files) {
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + "_" + f.getOriginalFilename();
            File file = new File(path + fileName);
            f.transferTo(file);

            String url = "http://192.168.10.51:8081/PostImg/" + fileName;

        }
    }

}
