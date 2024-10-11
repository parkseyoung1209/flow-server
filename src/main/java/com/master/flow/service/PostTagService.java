package com.master.flow.service;

import com.master.flow.model.dao.PostTagDAO;
import com.master.flow.model.vo.PostTag;
import com.master.flow.model.vo.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostTagService {

    @Autowired
    private PostTagDAO postTagDAO;

    // 게시물 태그 추가
    public PostTag addTag(PostTag postTag){
        return postTagDAO.save(postTag);
    }

    // 게시물 삭제
    public void deleteAll(int postCode){
        postTagDAO.deleteById(postCode);
    }

    // 태그 코드로 게시물 조회
    public List<Post> viewPostsByTagCode(int tagCode) {
        return postTagDAO.findPostsByTagCode(tagCode);
    }

    // postCode로 태그list 조회
    public List<PostTag> certainPostTag(int postCode){
        return postTagDAO.findByPost_PostCode(postCode);
    }
}
