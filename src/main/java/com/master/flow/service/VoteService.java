package com.master.flow.service;

import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.VoteDAO;
import com.master.flow.model.vo.User;
import com.master.flow.model.vo.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class VoteService {

    @Autowired
    private VoteDAO voteDao;

    @Autowired
    private PostDAO postDao;

    // 로그인한 사람 정보 가져오기 - ID
    public int loginId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.isAuthenticated()){
            User user = (User) auth.getPrincipal();
            return user.getUserCode();
        }
        return 0;
    }

    // 투표
    public void vote (Vote vo){

    }

    // 투표 취소

    // 로그인한 사람의 투표체크 여부
    public Vote check(int userCode) {
        return voteDao.check(loginId());
    }


    // 전체 투표 현황
    public int voteCount (int voteCode) {
        return voteDao.count(voteCode);
    }

    // 찬성 투표 현황
    public int voteCountY (int voteCode) {
        return voteDao.countY(voteCode);
    }

    // 반대 투표 현황
    public int voteCountN (int voteCode){
        return voteDao.countN(voteCode);
    }
}
