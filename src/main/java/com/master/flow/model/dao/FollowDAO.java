package com.master.flow.model.dao;

import com.master.flow.model.dto.FollowDTO;
import com.master.flow.model.vo.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.HashSet;

public interface FollowDAO extends JpaRepository<Follow, FollowDTO>, QuerydslPredicateExecutor {
    @Query("SELECT f FROM Follow f")
    HashSet<Follow> findAllFollowSet();
}
