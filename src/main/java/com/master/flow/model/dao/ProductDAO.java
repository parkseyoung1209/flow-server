package com.master.flow.model.dao;

import com.master.flow.model.vo.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDAO extends JpaRepository<Product, Integer> {
}
