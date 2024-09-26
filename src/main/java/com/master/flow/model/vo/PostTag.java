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
public class PostTag {
    @Id
    @Column(name="POST_TAG_CODE")
    private int postTagCode;

//    게시물 번호
    @Column(name="POST_CODE")
    private int postCode;

//    태그 코드
    @Column(name="TAG_CODE")
    private int tagCode;
}
