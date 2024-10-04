package com.master.flow.model.id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

// 복합키를 위한 아이디 클래스를 별도로 만들었습니다.
public class FollowId implements Serializable {
    private int followingUser;
    private int followerUser;
}
