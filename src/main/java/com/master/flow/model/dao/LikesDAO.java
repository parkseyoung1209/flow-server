package com.master.flow.model.dao;

import com.master.flow.model.vo.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikesDAO extends JpaRepository<Likes, Integer> {
        @Query(value="SELECT * FROM likes WHERE POST_CODE = :postCode",nativeQuery = true)
    List<Likes> findByPostCode(@Param("postCode") int postCode);
}
