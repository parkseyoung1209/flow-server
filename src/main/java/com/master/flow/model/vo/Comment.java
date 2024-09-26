package com.master.flow.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @Column(name="COMMENT_CODE")
    private int commentCode;

//    댓글 내용
    @Column(name="COMMENT_DESC")
    private String commentDesc;

//    사진 URL
    @Column(name="COMMNET_IMG_URL")
    private String commentImgUrl;

//    작성 날짜 (데이터 타입 수정 필요할지도)
    @Column(name="COMMENT_DATE")
    private LocalDateTime commentDate;

//    삭제 유무
    @Column(name="COMMENT_DEL_YN")
    private String commentDelYn;

//    게시물 번호
    @Column(name="POST_CODE")
    private String postCode;

//    유저 코드
    @Column(name="USER_CODE")
    private String userCode;
}
