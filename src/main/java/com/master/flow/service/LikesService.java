package com.master.flow.service;

import com.master.flow.model.dao.LikesDAO;
import com.master.flow.model.vo.Likes;
import com.master.flow.model.vo.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikesService {

    @Autowired
    private LikesDAO likesDAO;

    // 좋아요한 게시물 조회
    public List<Post> getLikedPosts(int userCode) {
        List<Likes> likes = likesDAO.findByUser_UserCode(userCode);
        return likes.stream().map(like -> like.getPost()).toList();
    }

    // 좋아요 수 카운트
    public int countLikes(int postCode) {
        return (int) likesDAO.countByPost_PostCode(postCode);
    }
}
