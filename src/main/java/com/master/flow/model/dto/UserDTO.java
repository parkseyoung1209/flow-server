package com.master.flow.model.dto;

import com.master.flow.model.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserDTO {

    private String id;
    private String token;
    private boolean isFollowing;
    private User user;

    public UserDTO(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public UserDTO(User user, boolean isFollowing) {
        this.user = user;
        this.isFollowing = isFollowing;
    }
}
