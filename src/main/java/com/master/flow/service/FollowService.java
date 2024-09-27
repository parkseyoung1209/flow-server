package com.master.flow.service;

import com.master.flow.model.dao.FollowDAO;
import com.master.flow.model.vo.Follow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class FollowService {
    @Autowired
    private FollowDAO followDAO;

    // 추가 및 중복방지 로직
    public List<Follow> viewAllFollowList() {
        return followDAO.findAll();
    }   // 비교를 위한 모든 팔로우 관계가 있는 테이블 가져오기

    public boolean addFollowingRelative(Follow value) {
        List<Follow> list = viewAllFollowList();
        Set<String> followingSet = new HashSet<>(); // 관계를 더할 해쉬셋
        for(Follow follow : list) {
            //리스트에 있는 컬럼값들을 '_' 문자를 넣은 스트링으로 변환
            String code = follow.getFollowingUser() + "_" + follow.getFollowerUser();
            followingSet.add(code); //다 넣음
        }
        // 유저가 새로 넣은 데이터를 바탕으로 만든 임의 관계도
        String newCode = value.getFollowingUser() + "_" + value.getFollowerUser();

        /* 해쉬셋에 같은 관계도 문자열이 포함되어있으면 true인데 이걸 부정해서 false로 만듦
            반대로 포함이 안되어있다면 false 발사 후 부정처리해서 check를 true로
        */
        boolean check = !followingSet.contains(newCode);

        if(value.getFollowingUser() != value.getFollowerUser() && check) {
            followDAO.save(value);
            return true; // 이후 check 여부에 따라 중복 방지 + 같은 유저코드 추가도 처리 후 true 발사
        }
        return false; // 중복 처리에 걸려서 false 발사
    }
}
