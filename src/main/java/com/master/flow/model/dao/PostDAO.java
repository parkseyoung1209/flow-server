package com.master.flow.model.dao;

import com.master.flow.model.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostDAO extends JpaRepository<Post, Integer> {
    @Query(value = "SELECT * FROM post WHERE post_type = 'vote'", nativeQuery = true)
    List<Post> findByPostTypesVote();

    @Query("SELECT post FROM Post post WHERE post.postType = :vote")
    List<Post> findPostTypesByVote(@Param("vote") String vote);

}
