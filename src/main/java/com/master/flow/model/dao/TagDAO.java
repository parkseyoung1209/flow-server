package com.master.flow.model.dao;

import com.master.flow.model.vo.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagDAO extends JpaRepository<Tag, Integer> {

    // 특정 포스트에 연결된 태그를 조회하는 메서드
    @Query("SELECT t FROM Tag t JOIN PostTag pt ON t.tagCode = pt.tagCode WHERE pt.postCode = :postCode")
    List<Tag> findTagsByPostCode(@Param("postCode") int postCode);
}
