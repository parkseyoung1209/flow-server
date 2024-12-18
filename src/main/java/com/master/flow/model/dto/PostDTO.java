package com.master.flow.model.dto;

import com.master.flow.model.vo.Product;
import com.master.flow.model.vo.Tag;
import com.master.flow.model.vo.User;
import com.master.flow.model.vo.VoteDesc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor
@AllArgsConstructor @Builder
public class PostDTO {
    // user 관련?
    private int userCode;
    private User user;

    // post 관련
    private int postCode;
    private String postType;
    private String postDesc;
    private String postPublicYn;
    private LocalDateTime postDate;

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
    private List<Tag> tags;

    private int likeCount;
    private int collectionCount;

    // 투표 관련
    private int voteCount; // 전체 투표 수
    private int yCount; // 찬성 투표 수
    private int nCount; // 반대 투표 수

    private String voteTextFirst; // 투표 내용
    private String voteTextSecond;
}
