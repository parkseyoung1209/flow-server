package com.master.flow.service;

import com.master.flow.model.dao.FollowDAO;
import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.dto.FollowDTO;
import com.master.flow.model.vo.Follow;
import com.master.flow.model.vo.QFollow;
import com.master.flow.model.vo.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FollowService {
    @Autowired
    private FollowDAO followDAO;

    @Autowired
    private UserDAO userDAO;

    // 전체 팔로우 테이블 가져오기
    public HashSet<Follow> findAllFollowSet() {
        return followDAO.findAllFollowSet();
    }


    // 로그인된 유저의 프라이머리키와 팔로우할 유저의 프라이머리키를 받아서 객체 생성
    public Follow existFollow(int followingUserCode, int followerUserCode) {
        User followingUser = userDAO.findById(followingUserCode).orElse(null);
        User followerUser = userDAO.findById(followerUserCode).orElse(null);
        Follow follow = Follow.builder()
                .followingUser(followingUser)
                .followerUser(followerUser)
                .build();
        return follow;
    }
    //전체 팔로우 해쉬셋에 새로 생성한 객체의 데이터가 포함되어있는지 확인
    public boolean checkLogic(int followingUserCode, int followerUserCode) {
        return findAllFollowSet().contains(existFollow(followingUserCode, followerUserCode));
    }
    //새로운 팔로우 관계 생성
    public boolean addFollowRelative(int followingUserCode, int followerUserCode) {
        if(checkLogic(followingUserCode, followerUserCode)) {
            return false; // 이미 존재한다면 false 후 컨트롤러로
        } else {
            followDAO.save(existFollow(followingUserCode, followerUserCode));
            return true; // 존재하지 않는다면 새로운 팔로우 관계 생성하고 컨트롤러로 ㄱㄱ
        }
    }
    // 언팔로우
    public boolean unFollow(int followingUserCode, int followerUserCode) {
        if(checkLogic(followingUserCode, followerUserCode)) {
            followDAO.delete(existFollow(followingUserCode, followerUserCode));
            return true; // 객체 존재여부 메서드로 참일시 관계 삭제 후 컨트롤러로
        } else {
            return false; // 아니면 false하고 컨트롤러 ㄱㄱ
        }
    }

    // 나를 팔로우한 인간들과 내가 팔로우한 인간들 나누는 메서드
    public BooleanBuilder selectFollowingOrFollower(int code, boolean check) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFollow qFollow = QFollow.follow;
        BooleanExpression expression1 = qFollow.followingUser.userCode.eq(code);
        BooleanExpression expression2 = qFollow.followerUser.userCode.eq(code);
        if(check) {
            booleanBuilder.and(expression1);
        } else {
            booleanBuilder.and(expression2);
        }
        return booleanBuilder;
    }

    //내가 팔로우한 인간들의 수와 인간들 전체 목록 dto발사
    public FollowDTO viewMyFollower(int followingUserCode) {
        List<Follow> follows = (List<Follow>) followDAO.findAll(selectFollowingOrFollower(followingUserCode, true));
        List<User> list = follows.stream()
                .map(Follow :: getFollowerUser)
                .collect(Collectors.toList());
        return new FollowDTO(list.size(), list);
    }
    //위랑 반대
    public FollowDTO followMeUsers (int followerUserCode) {
        List<Follow> follows = (List<Follow>) followDAO.findAll(selectFollowingOrFollower(followerUserCode, false));
        List<User> list = follows.stream()
                .map(Follow :: getFollowingUser)
                .collect(Collectors.toList());
        return new FollowDTO(list.size(), list);
    }
}
