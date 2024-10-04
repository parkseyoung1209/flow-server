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
public class Tag {
    @Id
    @Column(name="TAG_CODE")
    private int tagCode;

//    태그 분류 (계절, 스타일 등)
    @Column(name="TAG_TYPE")
    private String tagType;

//    태그명
    @Column(name="TAG_NAME")
    private String tagName;
}
