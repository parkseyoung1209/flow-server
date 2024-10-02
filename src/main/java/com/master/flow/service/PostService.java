package com.master.flow.service;

import com.master.flow.model.dao.CommentReportDAO;
import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.PostImgDAO;
import com.master.flow.model.dao.ProductDAO;
import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import com.master.flow.model.vo.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    @Autowired
    private PostDAO postDao;

    // 게시물 전체 조회
    public List<Post> viewAll(String sort) {
        List<Post> allPosts = postDao.findAll();

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
        log.info("vote : " + postDao.findByPostTypesVote());
        return postDao.findByPostTypesVote();
    }

    // 게시물 좋아요순으로 조회
    // public List<Post> getPostsOrderedByLikes() {
    //     List<Post> allPosts = postDao.findAll();

    //     return allPosts.stream()
    //             .sorted((p1, p2) -> Integer.compare(likesService.countLikes(p2.getPostCode()), likesService.countLikes(p1.getPostCode())))
    //             .collect(Collectors.toList());


    // 멀티 파트 파일 받으려면 아예 DTO 추가
    // DTO 하나에 받고자 하는 것들 다!
    // List<Multipartfile>
    // files[0]
    // files[1]

    // 게시물 업로드&수정
    public Post save(Post post) {
        // save : postCode(primary key)가 없으면 추가/ id가 있으면 수정으로 사용
        return postDao.save(post);
    }

    // 게시물 삭제
    public void delPost(int postCode) {
        postDao.deleteById(postCode);
    }

}

