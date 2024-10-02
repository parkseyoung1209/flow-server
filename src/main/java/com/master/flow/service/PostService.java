package com.master.flow.service;

import com.master.flow.model.dao.*;
import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.dto.PostInfoDTO;
import com.master.flow.model.dto.UserPostSummaryDTO;
import com.master.flow.model.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LikesDAO likesDAO;

    @Autowired
    private CollectionDAO collectionDAO;

    // 게시물 전체 조회
    public List<Post> viewAll(String sort) {
        List<Post> allPosts = postDAO.findAll();

        if ("newest".equalsIgnoreCase(sort)) {
            // 최신 순 정렬
            return allPosts.stream()
                    .sorted((p1, p2) -> p2.getPostDate().compareTo(p1.getPostDate()))
                    .collect(Collectors.toList());
        } else if ("oldest".equalsIgnoreCase(sort)) {
            // 오래된 순 정렬
            return allPosts.stream()
                    .sorted(Comparator.comparing(Post::getPostDate))
                    .collect(Collectors.toList());
        }

        // 기본적으로는 기존 순서 유지
        return allPosts;
    }

    // 투표 게시물 전체 조회
    public List<Post> postVoteViewAll(Post vo) {
        log.info("vote : " + postDAO.findByPostTypesVote());
        return postDAO.findByPostTypesVote();
    }

    // 게시물 좋아요순으로 조회
    // public List<Post> getPostsOrderedByLikes() {
    //     List<Post> allPosts = postDao.findAll();

    //     return allPosts.stream()
    //             .sorted((p1, p2) -> Integer.compare(likesService.countLikes(p2.getPostCode()), likesService.countLikes(p1.getPostCode())))
    //             .collect(Collectors.toList());



    // 게시물 업로드&수정
    public Post save(Post post) {
        // save : postCode(primary key)가 없으면 추가/ id가 있으면 수정으로 사용
        return postDAO.save(post);
    }

    public void delPost(int postCode){
        postDAO.deleteById(postCode);
    }


    // 유저 코드로 게시물 조회
    public UserPostSummaryDTO getPostListByUser (int userCode){
        Optional<User> user = userDAO.findById(userCode);

        List<Post> post = postDAO.findByUser_UserCode(userCode);

        List<PostInfoDTO> postInfoList = post.stream().map(posts -> {
            int likeCount = likesDAO.countByPost(posts);
            int collectionCount = collectionDAO.countByPost(posts);

            return new PostInfoDTO(posts, likeCount, collectionCount);
        }).collect(Collectors.toList());

        int totalSavedPost = postInfoList.size();

        return new UserPostSummaryDTO(postInfoList, totalSavedPost);

    }

}

