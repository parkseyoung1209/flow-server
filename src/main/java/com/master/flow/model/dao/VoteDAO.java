package com.master.flow.model.dao;

import com.master.flow.model.vo.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteDAO extends JpaRepository<Vote, Integer> {
}
