package com.master.flow.service;

import com.master.flow.model.dao.*;
import com.master.flow.model.dto.PostInfoDTO;
import com.master.flow.model.dto.UserPostSummaryDTO;
import com.master.flow.model.vo.Likes;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import com.master.flow.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

@Service
public class LikesService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private PostImgDAO postImgDAO;

    @Autowired
    private LikesDAO likesDAO;

    @Autowired
    private CollectionDAO collectionDAO;

    @Autowired
    private UserDAO userDAO;

    public boolean toggleLikeWithoutUser(User user, Post post) {
        // Post가 데이터베이스에 존재하지 않으면 에러 발생
        if (!postDAO.existsById(post.getPostCode())) {
            throw new IllegalArgumentException("Post가 존재하지 않습니다.");
        }

        // 전달된 유저가 이미 존재하는지 확인 (이 부분을 수정)
        User existingUser = userDAO.findById(user.getUserCode())
                .orElseThrow(() -> new IllegalArgumentException("User가 존재하지 않습니다."));

        // 이미 좋아요가 눌러져 있는지 확인
        Optional<Likes> existingLike = likesDAO.findByUserAndPost(existingUser, post);

        if (existingLike.isPresent()) {
            // 이미 좋아요가 눌러져 있는 경우 삭제
            likesDAO.delete(existingLike.get());
            return false;
        } else {
            // 새로 좋아요 추가
            Likes like = Likes.builder()
                    .user(existingUser)
                    .post(post)
                    .build();
            likesDAO.save(like);
            return true;
        }
    }
    public int countLikesByPost(Post post) {
        return likesDAO.countByPost(post);
    }

    // 좋아요 수 높은 순으로 게시물 조회
    public Page<Post> viewAllOrderByLikes(Pageable pageable) {
        // 좋아요 수 높은 순으로 게시물을 조회하면서, postType이 'vote'인 게시물 제외
        return likesDAO.findAllOrderByLikesAndPostTypeNotVote(pageable);
    }

    // 좋아요 수 카운트
    public int countLikes(int postCode) {
        return (int) likesDAO.countByPost_PostCode(postCode);
    }
    public List<Likes> showAllLikes() {return likesDAO.findAll();}

    public void delLike(int likesCode) {
        likesDAO.deleteById(likesCode);
    }

    // 유저가 좋아요한 게시물 조회
    public UserPostSummaryDTO getPostListByUser (int userCode) {

        Optional<User> user = userDAO.findById(userCode);

        List<Likes> likes = likesDAO.findByUser_UserCode(userCode);

        List<PostInfoDTO> postInfoList = likes.stream().map(like -> {
            Post post = like.getPost();
            List<PostImg> postImgs = postImgDAO.findByPost_PostCode(post.getPostCode());
            int likeCount = likesDAO.countByPost(post);
            int collectionCount = collectionDAO.countByPost(post);

            return new PostInfoDTO(post, likeCount, collectionCount, postImgs);
        }).collect(Collectors.toList());

        int totalSavedPost = postInfoList.size();

        return new UserPostSummaryDTO(postInfoList, totalSavedPost);

    }
}
