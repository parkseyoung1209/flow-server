package com.master.flow.model.dao;

import com.master.flow.model.vo.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionDAO extends JpaRepository<Collection, Integer> {
}
