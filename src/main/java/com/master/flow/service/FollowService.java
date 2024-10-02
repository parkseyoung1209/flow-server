package com.master.flow.service;

import com.master.flow.model.dao.FollowDAO;
import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.vo.Follow;
import com.master.flow.model.vo.QFollow;
import com.master.flow.model.vo.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class FollowService {
    @Autowired
    private FollowDAO followDAO;

    @Autowired
    private UserDAO userDAO;


    public Follow existFollow(int followingUserCode, int followerUserCode) {
        User followingUser = userDAO.findById(followingUserCode).orElse(null);
        User followerUser = userDAO.findById(followerUserCode).orElse(null);

        Follow follow = Follow.builder()
                .followingUser(followingUser)
                .followerUser(followerUser)
                .build();
        return follow;
    }

    public boolean checkLogic(int followingUserCode, int followerUserCode) {
        return followDAO.findAllFollowSet().contains(existFollow(followingUserCode,followerUserCode));
    }

    public boolean addFollowRelative(int followingUserCode, int followerUserCode) {
        if(checkLogic(followingUserCode,followerUserCode)) {
            return false;
        } else {
            followDAO.save(existFollow(followingUserCode, followerUserCode));
            return true;
        }
    }

    public boolean unFollow(int followingUserCode, int followerUserCode) {
        if(checkLogic(followingUserCode,followerUserCode)) {
            followDAO.delete(existFollow(followingUserCode,followerUserCode));
            return true;
        } else {
            return false;
        }
    }

    public void viewMyFollower(int followingUserCode) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QFollow qFollow = QFollow.follow;

        BooleanExpression expression = qFollow.followingUser.userCode.eq(followingUserCode);
        booleanBuilder.and(expression);

        List<Follow> follows = (List<Follow>) followDAO.findAll(booleanBuilder);
        List<User> list = follows.stream()
                .map(Follow :: getFollowerUser)
                .collect(Collectors.toList());
    }

    // 추가 및 중복방지 로직
    /*
    // 비교를 위한 모든 팔로우 관계가 있는 테이블 가져오기
    public ArrayList<Follow> viewAllFollowList() {
        return (ArrayList<Follow>) followDAO.findAll();
    }
    // 유저가 새로 넣은 데이터를 바탕으로 만든 임의 관계도
    private String getUserRelative(Follow value) {
        return value.getFollowingUser() + "_" + value.getFollowerUser();
    }
    /* 해쉬셋에 같은 관계도 문자열이 포함되어있으면 true인데 이걸 부정해서 false로 만듦
           반대로 포함이 안되어있다면 false 발사 후 부정처리해서 check를 true로 */
//    private boolean checkFollowing(Set<String> followingSet, String value) {
//        return !followingSet.contains(value);
//    }
    /*
    //팔로우 전체 테이블을 가져와서 스트링 데이터를 넣은 해쉬셋으로 변환
    public Set<String> getAllFollowSet() {
        ArrayList<Follow> list = viewAllFollowList();
        Set<String> followingSet = new HashSet<>(); // 관계를 더할 해쉬셋

        for (Follow follow : list) {
            //리스트에 있는 컬럼값들을 '_' 문자를 넣은 스트링으로 변환
            String code = follow.getFollowingUser() + "_" + follow.getFollowerUser();
            followingSet.add(code); //다 넣음
        }
        return followingSet;
    }
    public boolean searchFollow(Follow value) {
        Set<String> followingSet = getAllFollowSet();
        String newCode = getUserRelative(value);
        String[] reverse = newCode.split("_");
        String reverseCode = reverse[1] + "_" + reverse[0];
        boolean existCheck = checkFollowing(followingSet, reverseCode);
        return existCheck;
    }
    public Follow test(Follow value) {
        for(Follow follow : viewAllFollowList()) {
            if((follow.getFollowingUser() == value.getFollowerUser()) && follow.getFollowerUser() == value.getFollowingUser()) {
                return follow;
            }
        }
        return null;
    }
    // 추가 메서드
    public boolean addFollowingRelative(Follow value) {
        Set<String> followingSet = getAllFollowSet();
        String newCode = getUserRelative(value);
        boolean check = checkFollowing(followingSet, newCode);

        if(value.getFollowingUser() != value.getFollowerUser() && check && searchFollow(value) && checkFollow(value) == null) {
            followDAO.save(value);
            return true; // 이후 check 여부에 따라 중복 방지 + 같은 유저코드 추가도 처리 후 true 발사
        }
        return false; // 중복 처리에 걸려서 false 발사
    }
    public boolean FollowBack(Follow value) {
        if(searchFollow(value) == false  && checkFollow(value) == null) {
            Follow existFollow = test(value);
            int a = existFollow.getFollowingUser();
            int b = existFollow.getFollowerUser();
            int c = 27749*a + b;

            existFollow.setFollowingUser(c);
            existFollow.setFollowerUser(c);
            followDAO.save(existFollow);
            return true;
        }
        else return false;
    }
    public Follow checkFollow(Follow value) {
        int a = value.getFollowingUser();
        int b = value.getFollowerUser();
        Follow unfollow = null;
        for(Follow follow : viewAllFollowList()) {
            if(follow.getFollowingUser() == (27749*a+b)) {
                follow.setFollowingUser(b);
                follow.setFollowerUser(a);
                unfollow = follow;
                return unfollow;
            } else if(follow.getFollowingUser() == (27749*b+a)) {
                follow.setFollowingUser(b);
                follow.setFollowerUser(a);
                unfollow = follow;
                return unfollow;
            }
        }
        return null;
    }
    public boolean unFollow(Follow value) {
        Follow follow = checkFollow(value);
        if(follow != null) {
            followDAO.save(follow);
            return true;
        } else {
            followDAO.delete(value);
        }
        return false;
    }

    public Map<Integer, List<Integer>> MyfollowingUser(Follow value) {
        Integer count = followDAO.countFollower(value.getFollowingUser());
        List<Integer> followerUser = followDAO.myFollow(value.getFollowingUser());
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(count, followerUser);
        return map;
    }
    */
}
