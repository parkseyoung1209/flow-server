package com.master.flow.model.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
public class Product {
    @Id
    @Column(name="PRODUCT_CODE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productCode;
    
//    브랜드
    @Column(name="PRODUCT_BRAND")
    private String productBrand;

//    제품명
    @Column(name="PRODUCT_NAME")
    private String productName;

//    사이즈
    @Column(name="PRODUCT_SIZE")
    private String productSize;

//    구매처
    @Column(name="PRODUCT_BUY_FROM")
    private String productBuyFrom;

//    구매링크
    @Column(name="PRODUCT_LINK")
    private String productLink;

//    게시물 번호
    @ManyToOne
    @JoinColumn(name="POST_CODE")
    private Post post;
}
