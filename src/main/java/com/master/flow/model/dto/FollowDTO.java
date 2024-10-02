package com.master.flow.model.dto;

import com.master.flow.model.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowDTO implements Serializable {
    private User followingUser;
    private User followerUser;
}
