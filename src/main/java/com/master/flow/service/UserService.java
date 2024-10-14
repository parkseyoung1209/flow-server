package com.master.flow.service;

import com.master.flow.config.TokenProvider;
import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserDAO userDao;

    @Autowired
    private TokenProvider tokenProvider;

    // 사용자 정보 가져오는 메서드
    private User getUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null && auth.isAuthenticated()){
            return (User) auth.getPrincipal();
        }

        return null;
    }
    
    // 회원가입
    public void registerUser(User vo){
        vo.setUserBanStatus("N");
        vo.setUserManagerCode("N");
        userDao.save(vo);
    }

    // 회원가입시 중복체크
    public boolean duplicateCheck(String userEmail, String userPlatform){
        User user = userDao.duplicateCheck(userEmail, userPlatform).orElse(null);

        if(user == null) return true;
        return false;
    }

    // 로그인
    public Map<String, String> login(User vo){
        User user = userDao.duplicateCheck(vo.getUserEmail(), vo.getUserPlatform()).get();

        Map<String, String> response = new HashMap<>();
        String token = tokenProvider.create(user);
        response.put("token", token);

        int banCount = user.getUserBanCount();

//        밴 상태일때 로그인 금지 (4회 이상은 그냥 금지)
        if(user.getUserBanStatus().equals("Y")) {
            if(banCount == 1) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusDays(7));
                if(duration.isPositive()) response.put("ban", "Y");
                else if(duration.isNegative()) response.put("ban", "N");
            } else if(banCount == 2) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusDays(15));
                if(duration.isPositive()) response.put("ban", "Y");
                else if(duration.isNegative()) response.put("ban", "N");
            } else if(banCount == 3) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusMinutes(1));
                if(duration.isPositive()) response.put("ban", "Y");
                else if(duration.isNegative()) response.put("ban", "N");
            } else if(banCount >= 4) response.put("ban", "Y");
        } else if(user.getUserBanStatus().equals("N")) response.put("ban", "N");

        if(response.get("ban").equals("N")){
            user.setUserBanStatus("N");
            user.setUserBanDate(null);
            userDao.save(user);
        }

        return response;
    }

    // user 정보 가져오기
    public User findUser(int code){
        return userDao.findById(code).get();
    }
    public User findUser(){
        return userDao.findById(getUser().getUserCode()).get();
    }

//    유저 신고하기
    public void banUser(int userCode) {
        String userBanStatus = "Y";
        userDao.banUser(userCode, userBanStatus);
    }

    // 유저 탈퇴
    public void deleteUser(){
        userDao.deleteById(getUser().getUserCode());
    }

    // 유저 정보 수정
    public void updateUser(User vo){
        userDao.save(vo);
    }
}
