package com.master.flow.model.dto;

import com.master.flow.model.vo.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostInfoDTO {

    private Post post;
    private int likeCount;
    private int collectionCount;
}
