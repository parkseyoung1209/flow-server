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
        List<Post> posts = postDAO.findPostsByFilters(
                searchDTO.getUserJob(),
                searchDTO.getUserGender(),
                searchDTO.getUserHeightMin(),
                searchDTO.getUserHeightMax(),
                searchDTO.getUserWeightMin(),
                searchDTO.getUserWeightMax(),
                searchDTO.getTagCode()
        );

        return posts.stream().map(post -> {
            List<PostImg> postImgs = postImgDAO.findByPost_PostCode(post.getPostCode());
            int likeCount = likesDAO.countByPost(post);
            int collectionCount = collectionDAO.countByPost(post);

            return new PostInfoDTO(post, likeCount, collectionCount, postImgs);
        }).collect(Collectors.toList());
    }
}
