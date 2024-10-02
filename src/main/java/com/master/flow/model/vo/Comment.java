package com.master.flow.model.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @Column(name="COMMENT_CODE")
    private int commentCode;

//    댓글 내용
    @Column(name="COMMENT_DESC")
    private String commentDesc;

//    사진 URL
    @Column(name="COMMENT_IMG_URL")
    private String commentImgUrl;

//    작성 날짜 (데이터 타입 수정 필요할지도)
    @Column(name="COMMENT_DATE")
    private LocalDateTime commentDate;

//    삭제 유무
    @Column(name="COMMENT_DEL_YN")
    private String commentDelYn;

//    게시물 번호
    @ManyToOne
    @JoinColumn(name="POST_CODE")
    private Post post;

//    유저 코드
    @ManyToOne
    @JoinColumn(name="USER_CODE")
    private User user;

//    대댓글:부모 댓글
    @ManyToOne
    @JoinColumn(name="PARENT_COMMENT_CODE")
    private Comment parentComment;

//    대댓글:자식 댓글
    @OneToMany(mappedBy = "parentComment")
    private List<Comment> replies;
}
