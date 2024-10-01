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
public class Vote {
    @Id
    @Column(name="VOTE_CODE")
    private int voteCode;

//    찬반 여부
    @Column(name="VOTE_YN")
    private String voteYn;

//    게시물 번호
    @ManyToOne
    @JoinColumn(name="POST_CODE")
    private Post post;

//    유저 코드
    @ManyToOne
    @JoinColumn(name="USER_CODE")
    private User user;
}
