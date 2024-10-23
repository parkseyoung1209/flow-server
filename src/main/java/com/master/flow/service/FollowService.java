package com.master.flow.service;

import com.master.flow.model.dao.FollowDAO;
import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.PostImgDAO;
import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.dto.FollowDTO;
import com.master.flow.model.dto.PostInfoDTO;
import com.master.flow.model.dto.UserDTO;
import com.master.flow.model.vo.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FollowService {
    @Autowired
    private FollowDAO followDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private PostImgDAO postImgDAO;

    @Autowired
    private JPAQueryFactory queryFactory;

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
    //전체 팔로우 해쉬셋에 existFollow로 새로 생성한 객체의 데이터가 포함되어있는지 확인
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
          return booleanBuilder.and(expression1);
        } else return booleanBuilder.and(expression2);
    }

    // 검색기능
    public List<User> searchUser(int code, String key) {
        QUser qUser = QUser.user;  // QueryDSL로 생성된 QUser 객체
        QFollow qFollow = QFollow.follow;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (key != null && !key.trim().isEmpty()) {
            booleanBuilder.and(qUser.userEmail.contains(key).or(qUser.userNickname.contains(key)));
        }
        List<User> users = queryFactory
                .select(qUser)
                .from(qFollow)
                .join(qUser).on(qFollow.followerUser.userCode.eq(qUser.userCode))
                .where(qFollow.followingUser.userCode.eq(code)
                        .and(booleanBuilder))
                .fetch();
        System.out.println(users.size());
        return users;
    }

    //내가 팔로우한 인간들의 수와 인간들 전체 목록 dto발사
    public FollowDTO viewMyFollower(int followingUserCode) {
        List<Follow> follows = (List<Follow>) followDAO.findAll(selectFollowingOrFollower(followingUserCode, true));
        List<UserDTO> list = follows.stream()
                .map(f -> {
                    User user = f.getFollowerUser();
                    boolean logic = checkLogic(followingUserCode, user.getUserCode());
                    return new UserDTO(user, logic);
        })
                .collect(Collectors.toList());
        return new FollowDTO(list.size(), (ArrayList<UserDTO>) list);
    }
    //위랑 반대
    public FollowDTO viewMyFollowers(int followerUserCode, String key) {
        QUser qUser = QUser.user;  // QueryDSL로 생성된 QUser 객체
        QFollow qFollow = QFollow.follow;  // QueryDSL로 생성된 QFollow 객체

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // 이메일이나 닉네임에 key가 포함된 유저를 필터링하는 조건 추가
        if (key != null && !key.trim().isEmpty()) {
            booleanBuilder.and(qUser.userEmail.contains(key)
                    .or(qUser.userNickname.contains(key)));  // 이메일이나 닉네임에 key 포함 조건
        }

        // QueryDSL로 나를 팔로우한 유저 리스트 조회
        List<User> users = queryFactory
                .select(qUser)
                .from(qFollow)
                .join(qUser).on(qFollow.followerUser.userCode.eq(qUser.userCode))  // Follow -> User 조인
                .where(qFollow.followingUser.userCode.eq(followerUserCode)          // 나를 팔로우한 유저
                        .and(booleanBuilder))                                       // 추가 조건
                .fetch();

        // User 리스트를 UserDTO 리스트로 변환
        List<UserDTO> userDTOList = users.stream()
                .map(user -> {
                    boolean logic = checkLogic(followerUserCode, user.getUserCode());  // 로직 처리
                    return new UserDTO(user, logic);  // User -> UserDTO 변환
                })
                .collect(Collectors.toList());

        // FollowDTO 반환
        return new FollowDTO(userDTOList.size(), (ArrayList<UserDTO>) userDTOList);
    }

    // 내가 팔로우한 유저의 게시글 조회
    public List<PostInfoDTO> getPostsFromFollowedUsers(int userCode) {
        // 해당 유저가 팔로우한 유저 목록 가져오기
        List<Follow> followedUsers = followDAO.findAllByFollowerUser_UserCode(userCode);
        List<Integer> followedUserCodes = followedUsers.stream()
                .map(follow -> follow.getFollowingUser().getUserCode())
                .collect(Collectors.toList());

        // 팔로우한 유저의 게시물 가져오기
        List<Post> posts = postDAO.findByUser_UserCodeIn(followedUserCodes);

        return posts.stream().map(post -> {
            List<PostImg> postImgs = postImgDAO.findByPost_PostCode(post.getPostCode());
            return new PostInfoDTO(post, 0, 0, postImgs); // likeCount와 collectionCount는 나중에 추가할 수 있습니다.
        }).collect(Collectors.toList());
    }
}
