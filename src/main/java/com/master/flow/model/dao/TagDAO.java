package com.master.flow.model.dao;

import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagDAO extends JpaRepository<Tag, Integer> {

    @Query("SELECT post FROM Post post " +
            "[INNER] JOIN PostTag postTag ON post.postCode = postTag.post.postCode " +
            "[INNER] JOIN Tag tag ON postTag.tag.tagCode = tag.tagCode " +
            "WHERE tag.tagName = :tagName")
    List<Post> findPostsByTag(@Param("tagName") String tagName);
}
