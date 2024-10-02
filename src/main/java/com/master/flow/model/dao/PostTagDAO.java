package com.master.flow.model.dao;

import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostTag;
import com.master.flow.model.vo.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagDAO extends JpaRepository<PostTag, Integer> {

    @Query("SELECT t FROM Tag t WHERE t.tagName = :tagName")
    Tag findByTagName(@Param("tagName") String tagName);

    @Query("SELECT pt.post FROM PostTag pt WHERE pt.tag.tagCode = :tagCode")
    List<Post> findPostsByTagCode(@Param("tagCode") int tagCode);
}
