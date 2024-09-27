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
public class Likes {
//    좋아요
    @Id
    @Column(name="LIKES_CODE")
    private int likesCode;

//    유저 코드
    @ManyToOne
    @JoinColumn(name="USER_CODE")
    private User user;

//    게시물 번호
    @ManyToOne
    @JoinColumn(name="POST_CODE")
    private Post post;

}
