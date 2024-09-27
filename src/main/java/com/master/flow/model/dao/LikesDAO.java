package com.master.flow.model.dao;

import com.master.flow.model.vo.Likes;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesDAO extends JpaRepository<Likes, Integer> {
    Optional<Likes> findByUserAndPost(User user, Post post);
    int countByPost(Post post);
}
