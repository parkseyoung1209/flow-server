package com.master.flow.model.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Builder
@Data @AllArgsConstructor @NoArgsConstructor
public class UserReport {
    @Id
    @Column(name="USER_REPORT_CODE")
    private int userReportCode;

    @Column(name="USER_REPORT_DESC")
    private String userReportDesc;

    @ManyToOne
    @JoinColumn(name="USER_CODE")
    private User user;
}
