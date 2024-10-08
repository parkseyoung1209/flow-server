package com.master.flow.service;

import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserDAO userDao;

    // 테스트용 코드
    public List<User> showAllUser(){
        return userDao.findAll();
    }
    
    // 회원가입
    public void registerUser(User vo){
        userDao.save(vo);
    }

    // 회원가입시 중복체크
    public boolean duplicateCheck(String userEmail, String userPlatform){
        User user = userDao.duplicateCheck(userEmail, userPlatform).orElse(null);

        if(user == null) return true;
        return false;
    }

    // 로그인
    public User login(String userEmail, String userPlatform){
        return userDao.duplicateCheck(userEmail, userPlatform).get();
    }

    // user 정보 가져오기
    public User findUser(int code){
//        log.info("code : " + code);
        return userDao.findById(code).get();
    }

    public boolean banUser(int code) {
        User user = userDao.findById(code).get();
        if(user.getUserBanStatus()=="Y") {
            return false;
        }
        return false;
    }
}
