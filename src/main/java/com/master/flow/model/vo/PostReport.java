package com.master.flow.model.vo;

import jakarta.persistence.*;
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
    @ManyToOne
    @JoinColumn(name="POST_CODE")
    private Post post;

//    유저 코드
    @ManyToOne
    @JoinColumn(name="USER_CODE")
    private User user;
}
