package com.master.flow.model.dto;

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
    private int userCode;

    // post 관련
    private int postCode;
    private String postType;
    private String postDesc;
    private String postPublicYn;

    // postImg 관련
    private List<PostImgDTO> postImgInfo;
    private List<String> imageUrls; // 이미지 URL 리스트

    // List<Multipartfile>
    // files[0]
    // files[1]
    private List<MultipartFile> imageFiles;

    // product 관련
    private List<Product> products;

    // Tag 관련
    private List<Integer> tagCodes;

    private int likeCount;
    private int collectionCount;

}
