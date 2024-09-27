package com.master.flow.service;

import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.vo.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostDAO postDao;

    @Autowired
    private LikesService likesService;

    // 게시물 전체 조회
    public List<Post> viewAll(String sort) {
        List<Post> allPosts = postDao.findAll();

        if ("newest".equalsIgnoreCase(sort)) {
            // 최신 순 정렬
            return allPosts.stream()
                    .sorted((p1, p2) -> p2.getPostDate().compareTo(p1.getPostDate())) // assuming getCreatedDate() returns a date
                    .collect(Collectors.toList());
        } else if ("oldest".equalsIgnoreCase(sort)) {
            // 오래된 순 정렬
            return allPosts.stream()
                    .sorted(Comparator.comparing(Post::getPostDate)) // assuming getCreatedDate() returns a date
                    .collect(Collectors.toList());
        }

        // 기본적으로는 기존 순서 유지
        return allPosts;
    }

    // 게시물 좋아요순으로 조회
    public List<Post> getPostsOrderedByLikes() {
        List<Post> allPosts = postDao.findAll();

        return allPosts.stream()
                .sorted((p1, p2) -> Integer.compare(likesService.countLikes(p2.getPostCode()), likesService.countLikes(p1.getPostCode())))
                .collect(Collectors.toList());
    }
}
