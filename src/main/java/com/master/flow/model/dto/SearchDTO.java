package com.master.flow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class SearchDTO {

    private List<String> userJob;
    private String userGender;
    private Integer userHeightMin;
    private Integer userHeightMax;
    private Integer userWeightMin;
    private Integer userWeightMax;

    private List<String> tags;

}
