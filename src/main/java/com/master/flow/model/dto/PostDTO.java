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
    private String postType;
    private String postDesc;
    private String postPublicYn;
    private int userCode;

    // private List<MultipartFile> files;
    private List<Product> products;

}
