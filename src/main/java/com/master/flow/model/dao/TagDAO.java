package com.master.flow.model.dao;

import com.master.flow.model.vo.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagDAO extends JpaRepository<Tag, Integer> {
}
