package com.master.flow.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostReport {
    @Id
    @Column(name="POST_REPORT_CODE")
    private int postReportCode;

//    신고 내용
    @Column(name="POST_REPORT_DESC")
    private String postReportDesc;

//    게시물 번호
    @Column(name="POST_CODE")
    private int postCode;

//    유저 코드
    @Column(name="USER_CODE")
    private int userCode;
}
