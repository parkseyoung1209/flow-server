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
public class Product {
    @Id
    @Column(name="PRODUCT_CODE")
    private int productCode;
    
//    브랜드
    @Column(name="PRODUCT_BRAND")
    private String productBrand;

//    제품명
    @Column(name="PRODUCT_NAME")
    private String productName;

//    사이즈
    @Column(name="PRODUCT_SIZE")
    private int productSize;

//    구매처
    @Column(name="PRODUCT_BUY_FROM")
    private String productBuyFrom;

//    구매링크
    @Column(name="PRODUCT_LINK")
    private String productLink;

//    게시물 번호
    @Column(name="POST_CODE")
    private int postCode;
}
