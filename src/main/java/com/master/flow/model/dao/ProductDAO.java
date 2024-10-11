package com.master.flow.model.dao;

import com.master.flow.model.vo.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product, Integer> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product WHERE post_code = :postCode", nativeQuery = true)
    public void deleteProductByPostCode(@Param("postCode") int postCode);

    List<Product> findByPost_PostCode(int postCode);
}
