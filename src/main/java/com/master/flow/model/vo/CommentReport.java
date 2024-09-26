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
public class CommentReport {
    @Id
    @Column(name="COMMENT_REPORT_CODE")
    private int commentReportCode;

//    신고 내용
    @Column(name="COMMENT_REPORT_DESC")
    private String commentReportDesc;

//    댓글 코드
    @Column(name="COMMENT_CODE")
    private int commentCode;

//    유저 코드
    @Column(name="USER_CODE")
    private int userCode;
}
