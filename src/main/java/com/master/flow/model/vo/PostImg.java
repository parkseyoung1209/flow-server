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
public class PostImg {
    @Id
    @Column(name="POST_IMG_CODE")
    private int postImgCode;

//    사진 URL
    @Column(name="POST_IMG_URL")
    private String postImgUrl;

//    게시물 번호
    @Column(name="POST_CODE")
    private int postCode;
}
