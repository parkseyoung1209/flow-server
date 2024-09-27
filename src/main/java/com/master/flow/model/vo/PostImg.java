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
public class PostImg {
    @Id
    @Column(name="POST_IMG_CODE")
    private int postImgCode;

//    사진 URL
    @Column(name="POST_IMG_URL")
    private String postImgUrl;

//    게시물 번호
    @ManyToOne
    @JoinColumn(name="POST_CODE")
    private Post post;
}
