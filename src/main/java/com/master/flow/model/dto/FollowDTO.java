package com.master.flow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 내가 팔로우한 사람들의 수와 유저들 or 나를 팔로우한 사람들의 수와 유저들 한꺼번에 보냄
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class FollowDTO {
    private int countFollower;
    private List<UserDTO> Follower;
}
