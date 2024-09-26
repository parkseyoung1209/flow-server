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
public class Vote {
    @Id
    @Column(name="VOTE_CODE")
    private int voteCode;

//    찬반 여부
    @Column(name="VOTE_YN")
    private String voteYn;

//    게시물 번호
    @Column(name="POST_CODE")
    private int postCode;

//    유저 코드
    @Column(name="USER_CODE")
    private int userCode;
}
