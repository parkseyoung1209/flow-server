package com.master.flow.model.vo;

import com.master.flow.model.dto.FollowDTO;
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
@IdClass(FollowDTO.class)
public class Follow {
    /*
    @Id
    @Column(name="FOLLOW_CODE")
    private int followCode;

    @Column(name="FOLLOWING_USER")
    private int followingUser;

    @Column(name="FOLLOWER_USER")
    private int followerUser;
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "FOLLOWING_USER", referencedColumnName = "USER_CODE")
    private User followingUser;

    @Id
    @ManyToOne
    @JoinColumn(name = "FOLLOWER_USER", referencedColumnName = "USER_CODE")
    private User followerUser;
}
