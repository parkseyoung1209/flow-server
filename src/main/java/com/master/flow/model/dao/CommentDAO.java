package com.master.flow.model.dao;

import com.master.flow.model.vo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentDAO extends JpaRepository<Comment, Integer> {
}
