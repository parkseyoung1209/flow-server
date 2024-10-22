package com.master.flow.model.dao;

import com.master.flow.model.vo.Likes;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface LikesDAO extends JpaRepository<Likes, Integer> {

    Optional<Likes> findByUserAndPost(User user, Post post);
    
    int countByPost(Post post);

    @Query(value = "SELECT * FROM likes WHERE user_code = :userCode ORDER BY likes_code desc", nativeQuery = true)
    List<Likes> findByUser_UserCode(@Param("userCode") int userCode); // 좋아요 누른 게시글 목록 조회

    long countByPost_PostCode(int postCode); // 특정 게시물에 대한 좋아요 수 카운트

    @Query(value="SELECT * FROM likes WHERE POST_CODE = :postCode",nativeQuery = true)
    List<Likes> findByPostCode(@Param("postCode") int postCode);
    
    // 좋아요 수 높은 순으로 게시물 조회
    @Query("SELECT post FROM Post post " +
            "INNER JOIN Likes likes ON post.postCode = likes.post.postCode " +
            "GROUP BY post.postCode ORDER BY COUNT(likes) DESC")
    List<Post> findAllOrderByLikes();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM likes WHERE post_code =:postCode",nativeQuery = true)
    public void deleteLikesByPostCode(@Param("postCode") int postCode);

}
