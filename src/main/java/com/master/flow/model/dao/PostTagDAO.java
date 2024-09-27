package com.master.flow.model.dao;

import com.master.flow.model.vo.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagDAO extends JpaRepository<PostTag, Integer> {
}
