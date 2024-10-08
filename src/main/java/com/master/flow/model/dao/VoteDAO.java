package com.master.flow.model.dao;

import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteDAO extends JpaRepository<Vote, Integer> {

    // 투표게시물 전제 투표 수
    @Query(value = "SELECT count(*) FROM vote WHERE post_code = :post_code" , nativeQuery = true)
    int count(@Param("post_code") int voteCount);

    // 투표게시물 찬성 투표 수
    @Query(value = "SELECT count(*) FROM vote WHERE post_public_yn = 'y' and post_code = :post_code", nativeQuery = true)
    int countY(@Param("post_code") int voteCountY);

    // 투표게시물 반대 투표 수

    // 투표게시물 반대 투표 수
}
