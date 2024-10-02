package com.master.flow.service;

import com.master.flow.model.dao.CollectionDAO;
import com.master.flow.model.dao.LikesDAO;
import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.dto.PostInfoDTO;
import com.master.flow.model.dto.UserPostSummaryDTO;
import com.master.flow.model.vo.Collection;
import com.master.flow.model.vo.Likes;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollectionService {
    @Autowired
    private CollectionDAO dao;

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LikesDAO likesDAO;

    public boolean toggleCollectionWithoutUser(User user, Post post) {
        // Post가 데이터베이스에 저장되지 않았다면 저장
        if (!postDAO.existsById(post.getPostCode())) {
            throw new IllegalArgumentException("Post가 존재하지 않습니다.");}
        Optional<Collection> existingCollection = dao.findByUserAndPost(user, post);

        if (existingCollection.isPresent()) {
            // 이미 좋아요가 눌러져 있는 경우 삭제
            dao.delete(existingCollection.get());
            return false;
        } else {
            Collection collection = Collection.builder()
                    .user(user)
                    .post(post)
                    .build();
            dao.save(collection);
            return true;
        }
    }
    public int countCollectionByPost(Post post) {
        return dao.countByPost(post);
    }

    public UserPostSummaryDTO getPostListByUser (int userCode)
    {
        Optional<User> user = userDAO.findById(userCode);

        List<Collection> collections = dao.findByUser(user);

        List<PostInfoDTO> postInfoList = collections.stream().map(collection -> {
            Post post = collection.getPost();
            int likeCount = likesDAO.countByPost(post);
            int collectionCount = dao.countByPost(post);

            return new PostInfoDTO(post, likeCount, collectionCount);
        }).collect(Collectors.toList());

        int totalSavedPost = postInfoList.size();

        return new UserPostSummaryDTO(postInfoList, totalSavedPost);
    }
}

