package com.master.flow.model.dto;

import com.master.flow.model.vo.Comment;
import com.master.flow.model.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class CommentDTO {

    private int commentCode;
    private String commentDesc;
    private String commentImgUrl;
    private LocalDateTime commentDate;
    private String commentDelYn;
    private int postCode;
    private int userCode;
    private String userNickname;
    private int parentCommentCode;
    private List<Comment> replies = new ArrayList<>(); // 자식댓글리스트!
}
