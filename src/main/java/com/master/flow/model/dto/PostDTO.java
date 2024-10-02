package com.master.flow.model.dto;

import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import com.master.flow.model.vo.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data @NoArgsConstructor
@AllArgsConstructor @Builder
public class PostDTO {
    // user 관련?

    // post 관련
    private int postCode;
    private String postType;
    private String postDesc;
    private String postPublicYn;
    private int userCode;

    // postImg 관련
    // 멀티 파트 파일 받으려면 아예 DTO 추가
    // DTO 하나에 받고자 하는 것들 다!
    // List<Multipartfile>
    // files[0]
    // files[1]
    private List<MultipartFile> imageFiles;

    // product 관련
    private List<Product> products;

    // Tag 관련
    private List<Integer> tagCodes;

}
