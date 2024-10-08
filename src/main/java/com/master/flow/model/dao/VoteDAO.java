package com.master.flow.model.dao;

import com.master.flow.model.vo.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteDAO extends JpaRepository<Vote, Integer> {

    // 로그인한 사람의 투표체크 여부
    @Query(value = "SELECT * FROM vote WHERE user_code = :userCode AND vote_yn = 'y' OR vote_yn = 'n'" , nativeQuery = true)
    Vote check(@Param("userCode") int userCode);

    // 투표게시물 전제 투표 수
    @Query(value = "SELECT count(*) FROM vote WHERE post_code = :post_code" , nativeQuery = true)
    int count(@Param("post_code") int voteCount);

    // 투표게시물 찬성 투표 수
    @Query(value = "SELECT count(*) FROM vote WHERE vote_yn = 'y' and post_code = :post_code", nativeQuery = true)
    int countY(@Param("post_code") int voteCountY);

    // 투표게시물 반대 투표 수
    @Query(value = "SELECT count(*) FROM vote WHERE vote_yn = 'n' and post_code = :post_code", nativeQuery = true)
    int countN(@Param("post_code") int voteCountN);


}
