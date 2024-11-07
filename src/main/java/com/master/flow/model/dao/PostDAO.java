package com.master.flow.model.dao;

import com.master.flow.model.vo.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostDAO extends JpaRepository<Post, Integer>, QuerydslPredicateExecutor<Post> {
    // post_public_yn = "Y" 인 경우 게시글(post)에서 투표게시물(vote)만 조회
    @Query(value = "SELECT * FROM post WHERE post_type = 'vote' and post_public_yn = 'Y' ORDER BY post_code desc", nativeQuery = true)
    List<Post> findByPostTypesVote();

    @Query(value = "SELECT * FROM post WHERE user_code = :userCode AND post_type = 'post' ORDER BY post_code desc", nativeQuery = true)
    List<Post> findByUser_UserCode(@Param("userCode") int userCode); // 유저 코드로 게시글 목록 조회

    @Query(value = "SELECT * FROM post WHERE user_code = :userCode AND post_type = 'vote' ORDER BY post_code desc", nativeQuery = true)
    List<Post> findByUser_UserVote(@Param("userCode") int userCode);

    // 카테고리별 게시물 조회
    @Query("SELECT p FROM Post p "
            + "JOIN p.user u "
            + "LEFT JOIN PostTag pt ON p.postCode = pt.postCode "
            + "WHERE "
            + "(:userJob IS NULL OR u.userJob IN :userJob) "
            + "AND (:userGender IS NULL OR u.userGender = :userGender) "
            + "AND (:userHeightMin IS NULL OR u.userHeight >= :userHeightMin) "
            + "AND (:userHeightMax IS NULL OR u.userHeight <= :userHeightMax) "
            + "AND (:userWeightMin IS NULL OR u.userWeight >= :userWeightMin) "
            + "AND (:userWeightMax IS NULL OR u.userWeight <= :userWeightMax) "
            + "AND (:tagCode IS NULL OR pt.tagCode IN :tagCode) "
            + "GROUP BY p "
            + "HAVING (:tagCodeSize = 0 OR COUNT(DISTINCT pt.tagCode) = :tagCodeSize)")
    List<Post> findPostsByFilters(
            @Param("userJob") List<String> userJob,
            @Param("userGender") String userGender,
            @Param("userHeightMin") Integer userHeightMin,
            @Param("userHeightMax") Integer userHeightMax,
            @Param("userWeightMin") Integer userWeightMin,
            @Param("userWeightMax") Integer userWeightMax,
            @Param("tagCode") List<Integer> tagCode,
            @Param("tagCodeSize") long tagCodeSize);

    // 게시물 페이징 조회
    Page<Post> findByUser_UserCodeIn(List<Integer> userCodes, Pageable pageable);
}
