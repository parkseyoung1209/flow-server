package com.master.flow.model.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Data
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
    @ManyToOne
    @JoinColumn(name="COMMENT_CODE")
    private Comment comment;

//    유저 코드
    @ManyToOne
    @JoinColumn(name="USER_CODE")
    private User user;
}
