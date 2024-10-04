package com.master.flow.model.dao;

import com.master.flow.model.vo.Likes;
import com.master.flow.model.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostDAO extends JpaRepository<Post, Integer>, QuerydslPredicateExecutor<Post> {
    // post_public_yn = "Y" 인 경우 게시글(post)에서 투표게시물(vote)만 조회
    @Query(value = "SELECT * FROM post WHERE post_type = 'vote' and post_public_yn = 'Y'", nativeQuery = true)
    List<Post> findByPostTypesVote();

    @Query(value = "SELECT * FROM post JOIN vote USING ( WHERE post_type = 'vote' and post_public_yn = 'Y'", nativeQuery = true)

    List<Post> findByUser_UserCode(int userCode); // 유저 코드로 게시글 목록 조회
}
