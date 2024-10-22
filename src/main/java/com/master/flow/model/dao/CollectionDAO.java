package com.master.flow.model.dao;

import com.master.flow.model.vo.Collection;
import com.master.flow.model.vo.Likes;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CollectionDAO extends JpaRepository<Collection, Integer> {
    Optional<Collection> findByUserAndPost(User user, Post post);
    int countByPost(Post post);

    // 특정 유저가 저장한 게시물 리스트 반환
    @Query(value = "SELECT * FROM collection WHERE user_code = :userCode ORDER BY collection_code desc", nativeQuery = true)
    List<Collection> findByUser(@Param("userCode") int userCode);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM COLLECTION WHERE post_code = :postCode",nativeQuery = true)
    public void deleteCollectionByPostCode(@Param("postCode") int postCode);
}
