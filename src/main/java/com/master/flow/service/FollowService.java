package com.master.flow.service;

import com.master.flow.model.dao.FollowDAO;
import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.vo.Follow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
    @Autowired
    private FollowDAO followDAO;

    @Autowired UserDAO userDAO;

    public void addFollowingRelative(Follow value) {
        if(!(value.getFollowerUser() == value.getFollowingUser())) {
            followDAO.save(value);
        }
    }
}
