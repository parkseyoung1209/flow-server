package com.master.flow.service;

import com.master.flow.model.dao.PostTagDAO;
import com.master.flow.model.vo.PostTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
