package com.master.flow.service;

import com.master.flow.model.dao.LikesDAO;
import com.master.flow.model.vo.Likes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikesService {
    @Autowired
    private LikesDAO likesDao;

    public List<Likes> showAllLikes() {return likesDao.findAll();}

    public void delLike(int likesCode) {
        likesDao.deleteById(likesCode);
    }
}
