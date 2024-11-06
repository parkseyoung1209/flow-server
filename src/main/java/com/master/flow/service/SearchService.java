package com.master.flow.service;

import com.master.flow.model.dao.*;
import com.master.flow.model.dto.PostInfoDTO;
import com.master.flow.model.dto.SearchDTO;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private PostImgDAO postImgDAO;

    @Autowired
    private LikesDAO likesDAO;

    @Autowired
    private CollectionDAO collectionDAO;

    public List<PostInfoDTO> searchPosts(SearchDTO searchDTO) {
        // 태그 개수 구하기
        long tagCodeSize = searchDTO.getTagCode() != null ? searchDTO.getTagCode().size() : 0;

        // tagCode로 필터링 - 모든 태그 조건을 만족하는 게시물만 조회
        List<Post> posts = postDAO.findPostsByFilters(
                searchDTO.getUserJob(),
                searchDTO.getUserGender(),
                searchDTO.getUserHeightMin(),
                searchDTO.getUserHeightMax(),
                searchDTO.getUserWeightMin(),
                searchDTO.getUserWeightMax(),
                searchDTO.getTagCode(),
                tagCodeSize
        );

        // Post를 PostInfoDTO로 변환
        return posts.stream().map(post -> {
            List<PostImg> postImgs = postImgDAO.findByPost_PostCode(post.getPostCode());
            int likeCount = likesDAO.countByPost(post);
            int collectionCount = collectionDAO.countByPost(post);

            return new PostInfoDTO(post, likeCount, collectionCount, postImgs);
        }).collect(Collectors.toList());
    }
}
