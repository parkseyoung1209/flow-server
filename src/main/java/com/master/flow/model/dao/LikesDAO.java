package com.master.flow.model.dao;

import com.master.flow.model.vo.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesDAO extends JpaRepository<Likes, Integer> {
}
