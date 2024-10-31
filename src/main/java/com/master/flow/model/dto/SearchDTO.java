package com.master.flow.model.dto;

import com.master.flow.model.vo.PostTag;
import com.master.flow.model.vo.Tag;
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
    private Integer tagCode;

}
