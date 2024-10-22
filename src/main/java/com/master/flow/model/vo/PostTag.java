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
public class PostTag {
    @Id
    @Column(name="POST_TAG_CODE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postTagCode;

//    게시물 번호
    @ManyToOne
    @JoinColumn(name="POST_CODE")
    private Post post;

//    태그 코드
    @ManyToOne(cascade = CascadeType.ALL )
    @JoinColumn(name="TAG_CODE")
    private Tag tag;
}
