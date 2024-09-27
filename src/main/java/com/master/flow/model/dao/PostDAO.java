package com.master.flow.model.dao;

import com.master.flow.model.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostDAO extends JpaRepository<Post, Integer> {
}
