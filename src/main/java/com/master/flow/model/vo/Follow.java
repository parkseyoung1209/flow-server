package com.master.flow.model.vo;

import com.master.flow.model.id.FollowId;
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
@IdClass(FollowId.class)
public class Follow {
    @Id
    @ManyToOne
    @JoinColumn(name = "FOLLOWING_USER", referencedColumnName = "USER_CODE")
    private User followingUser;

    @Id
    @ManyToOne
    @JoinColumn(name = "FOLLOWER_USER", referencedColumnName = "USER_CODE")
    private User followerUser;
}
