package com.master.flow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserPostSummaryDTO {
    private List<PostInfoDTO> postInfoList;
    private int totalPosts;
}
