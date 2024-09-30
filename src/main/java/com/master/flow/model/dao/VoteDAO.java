package com.master.flow.model.dao;

import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteDAO extends JpaRepository<Vote, Integer> {

}
