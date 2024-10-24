package com.master.flow.model.dao;

import com.master.flow.model.id.FollowId;
import com.master.flow.model.vo.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.HashSet;
import java.util.List;

public interface FollowDAO extends JpaRepository<Follow, FollowId>, QuerydslPredicateExecutor<Follow> {
    @Query("SELECT f FROM Follow f")
    HashSet<Follow> findAllFollowSet();

    List<Follow> findAllByFollowingUser_UserCode(int userCode);

}
