package com.master.flow.model.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
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

    @JsonIgnore
    @Column(name="POST_CODE", insertable = false, updatable = false)
    private Integer postCode;

//    게시물 번호
    @ManyToOne
    @JoinColumn(name="POST_CODE")
    private Post post;

    @JsonIgnore
    @Column(name="TAG_CODE", insertable = false, updatable = false)
    private Integer tagCode;

//    태그 코드
    @ManyToOne
    @JoinColumn(name="TAG_CODE")
    private Tag tag;
}
