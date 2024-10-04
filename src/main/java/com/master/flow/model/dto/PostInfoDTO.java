package com.master.flow.model.dto;

import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostInfoDTO {

    private Post post;
    private int likeCount;
    private int collectionCount;
    private List<PostImg> imageFiles;
}
