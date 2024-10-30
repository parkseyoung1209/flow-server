package com.master.flow.service;

import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.PostTagDAO;
import com.master.flow.model.dto.SearchDTO;
import com.master.flow.model.vo.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private PostTagDAO postTagDAO;

    public List<Post> searchPosts(SearchDTO searchDTO) {
        System.out.println("Received search DTO: " + searchDTO);

        List<String> jobs = searchDTO.getUserJob(); // 리스트로 변경
        String gender = searchDTO.getUserGender();
        Integer heightMin = searchDTO.getUserHeightMin();
        Integer heightMax = searchDTO.getUserHeightMax();
        Integer weightMin = searchDTO.getUserWeightMin();
        Integer weightMax = searchDTO.getUserWeightMax();
        Integer tagCode = searchDTO.getTagCode();

        // 카테고리별 게시물 조회
        List<Post> posts = postDAO.findPostsByFilters(
                (jobs != null && !jobs.isEmpty()) ? jobs : null, // 비어있을 경우 null
                gender,
                heightMin,
                heightMax,
                weightMin,
                weightMax,
                tagCode
        );

        // 태그 필터링
//        if (searchDTO.getTagCode() != null && !searchDTO.getTagCode().isEmpty()) {
//            List<Post> taggedPosts = postTagDAO.findPostsByTagNames(searchDTO.getTags());
//            posts.retainAll(taggedPosts);
//        }

        return posts;
    }

}
