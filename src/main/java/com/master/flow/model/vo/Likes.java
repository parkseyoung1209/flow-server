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
public class Likes {
//    좋아요
    @Id
    @Column(name="LIKES_CODE")
    private int likesCode;

//    유저 코드
    @Column(name="USER_CODE")
    private int userCode;

//    게시물 번호
    @Column(name="POST_CODE")
    private int postCode;

}
