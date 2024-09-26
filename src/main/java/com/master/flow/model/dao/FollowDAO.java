package com.master.flow.model.dao;

import com.master.flow.model.vo.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowDAO extends JpaRepository<Follow, Integer> {
}
