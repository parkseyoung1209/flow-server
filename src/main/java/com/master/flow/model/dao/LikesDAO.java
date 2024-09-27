package com.master.flow.model.dao;

import com.master.flow.model.vo.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesDAO extends JpaRepository<Likes, Integer> {

    List<Likes> findByUser_UserCode(int userCode); // 좋아요 누른 게시글 목록 조회

    long countByPost_PostCode(int postCode); // 특정 게시물에 대한 좋아요 수 카운트
}
