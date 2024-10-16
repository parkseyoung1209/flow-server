package com.master.flow.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private int userCode;
    private String userEmail;
    private String userPlatform;
    private String userNickname;
    private String userJob;
    private String userGender;
    private int userHeight;
    private int userWeight;
    private String userBodySpecYn;
    private String userProfileUrl;
    private String userManagerCode;
    private String userBanStatus;
    private LocalDateTime userBanDate;
    private int userBanCount;
    private MultipartFile imgFile;
}
