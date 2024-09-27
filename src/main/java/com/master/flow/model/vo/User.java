package com.master.flow.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class User {

    @Id
    @Column(name = "user_code")
    private int userCode;
    @Column(name = "user_email")
    private String userEmail;
    @Column(name = "user_platform")
    private String userPlatform;
    @Column(name = "user_nickname")
    private String userNickname;
    @Column(name = "user_job")
    private String userJob;
    @Column(name = "user_gender")
    private String userGender;
    @Column(name = "user_height")
    private int userHeight;
    @Column(name = "user_weight")
    private int userWeight;
    @Column(name = "user_body_spec_yn")
    private String userBodySpecYn;
    @Column(name = "user_profile_url")
    private String userProfileUrl;
    @Column(name = "user_manager_code")
    private String userManagerCode;
    @Column(name = "user_ban_status")
    private String userBanStatus;
    @Column(name = "user_ban_date")
    private Date userBanDate;
    @Column(name = "user_ban_count")
    private int userBanCount;
}
