package com.master.flow.model.dao;

import com.master.flow.model.vo.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowDAO extends JpaRepository<Follow, Integer> {
    @Query("SELECT count(*) FROM Follow WHERE followingUser= :followingUser")
    public int countFollower(int followingUser);

    @Query("SELECT followerUser From Follow WHERE followingUser= :followingUser")
    public List<Integer> myFollow(int followingUser);
}
