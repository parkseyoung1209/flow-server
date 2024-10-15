package com.master.flow.model.dao;

import com.master.flow.model.vo.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostDAO extends JpaRepository<Post, Integer>, QuerydslPredicateExecutor<Post> {
    // post_public_yn = "Y" 인 경우 게시글(post)에서 투표게시물(vote)만 조회
    @Query(value = "SELECT * FROM post WHERE post_type = 'vote' and post_public_yn = 'Y'", nativeQuery = true)
    List<Post> findByPostTypesVote();

    List<Post> findByUser_UserCode(int userCode); // 유저 코드로 게시글 목록 조회

    // 카테고리별 게시물 조회
    @Query("SELECT p FROM Post p JOIN p.user u WHERE "
            + "(:job IS NULL OR u.userJob = :job) "
            + "AND (:gender IS NULL OR u.userGender = :gender) "
            + "AND (:height IS NULL OR u.userHeight = :height)")
    List<Post> findPostsByFilters(
            @Param("job") String job,
            @Param("gender") String gender,
            @Param("height") Integer height
    );

    List<Post> findByUser_UserCodeIn(List<Integer> userCodes, Pageable pageable);
}
