package com.master.flow.service;

import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.TagDAO;
import com.master.flow.model.vo.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagDAO tagDao;

    // 태그로 게시물 조회
//    public List<Post> viewPostsByTag(String tagName) {
//        return tagDao.findPostsByTag(tagName);
//    }

}
