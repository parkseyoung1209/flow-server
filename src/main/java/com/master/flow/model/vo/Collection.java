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
public class Collection {
//    게시물 저장 코드
    @Id
    @Column(name="COLLECTION_CODE")
    private int collectionCode;

//    유저 코드
    @Column(name="USER_CODE")
    private int userCode;

//    게시물 번호
    @Column(name="POST_CODE")
    private int postCode;
}
