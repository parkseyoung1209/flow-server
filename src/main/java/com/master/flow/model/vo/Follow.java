package com.master.flow.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Follow {
    @Id
    @Column(name="FOLLOW_CODE")
    private int followCode;

    @Column(name="FOLLOWING_USER")
    private int followingUser;

    @Column(name="FOLLOWER_USER")
    private int followerUser;
}
