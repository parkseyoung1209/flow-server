package com.master.flow.service;

import com.master.flow.model.dao.PostDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    @Autowired
    private PostDAO postDao;

    public void delPost(int postCode){
        postDao.deleteById(postCode);
    }
}
