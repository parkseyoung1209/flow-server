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
public class VoteDesc {

    @Id
    @Column(name = "vote_desc_code")
    private int voteDescCode;

    @Column(name = "vote_text_first")
    private String voteTextFirst;

    @Column(name = "vote_text_second")
    private String voteTextSecond;

    @ManyToOne
    @JoinColumn(name = "post_code")
    private Post post;
}
