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
    @Column(name="USER_CODE")
    private int userCode;

//    이메일
    @Column(name="USER_EMAIL")
    private String userEmail;

//    가입 채널 (카카오, 네이버, 구글)
    @Column(name="USER_PLATFORM")
    private String userPlatform;

//    닉네임
    @Column(name="USER_NICKNAME")
    private String userNickname;

//    직종
    @Column(name="USER_JOB")
    private String userJob;

//    성별
    @Column(name="USER_GENDER")
    private String userGender;

//    키
    @Column(name="USER_HEIGHT")
    private int userHeight;

//    체중
    @Column(name="USER_WEIGHT")
    private int userWeight;

//    신체 스펙 공개 여부
    @Column(name="USER_BODY_SPEC_YN")
    private String userBodySpecYn;

//    프로필 사진
    @Column(name="USER_PROFILE_URL")
    private String userProfileUrl;

//    관리자 유무
    @Column(name="USER_MANAGER_CODE")
    private String userManagerCode;

//    제재 상태
    @Column(name="USER_BAN_STATUS")
    private String userBanStatus;

//  java.util 인지 java.sql인지 모름
//    제재 날짜
    @Column(name="USER_BAN_DATE")
    private Date userBanDate;

//    제재 횟수
    @Column(name="USER_BAN_COUNT")
    private int userBanCount;

}
