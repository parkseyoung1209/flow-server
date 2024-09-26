package com.master.flow.service;

import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDAO userDao;

    public List<User> showAllUser(){
        return userDao.findAll();
    }

}
