package com.master.flow.model.dao;

import com.master.flow.model.vo.VoteDesc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteDescDAO extends JpaRepository<VoteDesc, Integer> {
}
