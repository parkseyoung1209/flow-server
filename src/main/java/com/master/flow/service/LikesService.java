package com.master.flow.service;

import com.master.flow.model.dao.LikesDAO;
import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.vo.Likes;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
@Service
public class LikesService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LikesDAO likesDAO;

    public boolean toggleLikeWithoutUser(User user, Post post) {
        // Post가 데이터베이스에 저장되지 않았다면 저장
        if (!postDAO.existsById(post.getPostCode())) {
            throw new IllegalArgumentException("Post가 존재하지 않습니다.");}
        Optional<Likes> existingLike = likesDAO.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            // 이미 좋아요가 눌러져 있는 경우 삭제
            likesDAO.delete(existingLike.get());
            return false;
        } else {
            Likes like = Likes.builder()
                    .user(user)
                    .post(post)
                    .build();
            likesDAO.save(like);
            return true;
        }
    }
    public int countLikesByPost(Post post) {
        return likesDAO.countByPost(post);
    }

    // 좋아요한 게시물 조회
    public List<Post> getLikedPosts(int userCode) {
        List<Likes> likes = likesDAO.findByUser_UserCode(userCode);
        return likes.stream().map(like -> like.getPost()).toList();
    }

    // 좋아요 수에 따라 게시물 조회
//    public List<Post> viewAllOrderByLikes() {
//        return likesDAO.findAllOrderByLikes();
//    }

    // 좋아요 수 카운트
    public int countLikes(int postCode) {
        return (int) likesDAO.countByPost_PostCode(postCode);
    }
    public List<Likes> showAllLikes() {return likesDAO.findAll();}

    public void delLike(int likesCode) {
        likesDAO.deleteById(likesCode);
    }
}
