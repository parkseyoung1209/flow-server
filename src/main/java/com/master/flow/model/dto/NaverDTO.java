package com.master.flow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NaverDTO {
    private String clientId;
    private String clientSecret;
    private String code;
    private String state;
}
