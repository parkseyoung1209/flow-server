package com.master.flow.model.dao;

import com.master.flow.model.vo.Likes;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface LikesDAO extends JpaRepository<Likes, Integer> {

    Optional<Likes> findByUserAndPost(User user, Post post);
    
    int countByPost(Post post);

    List<Likes> findByUser_UserCode(int userCode); // 좋아요 누른 게시글 목록 조회

    long countByPost_PostCode(int postCode); // 특정 게시물에 대한 좋아요 수 카운트
}
