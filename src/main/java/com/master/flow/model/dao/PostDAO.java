package com.master.flow.model.dao;

import com.master.flow.model.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostDAO extends JpaRepository<Post, Integer>, QuerydslPredicateExecutor<Post> {
    @Query(value = "SELECT * FROM post WHERE post_type = 'vote'", nativeQuery = true)
    List<Post> findByPostTypesVote();

    @Query("SELECT post FROM Post post WHERE post.postType = :vote")
    List<Post> findPostTypesByVote(@Param("vote") String vote);

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

}
