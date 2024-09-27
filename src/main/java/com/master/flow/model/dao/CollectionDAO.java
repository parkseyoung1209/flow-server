package com.master.flow.model.dao;

import com.master.flow.model.vo.Collection;
import com.master.flow.model.vo.Likes;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollectionDAO extends JpaRepository<Collection, Integer> {
    Optional<Collection> findByUserAndPost(User user, Post post);
    int countByPost(Post post);
}
