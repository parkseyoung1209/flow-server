package com.master.flow.service;

import com.master.flow.follow.helperClass.ConvertToInitialsFromNameList;
import com.master.flow.model.dao.FollowDAO;
import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.PostImgDAO;
import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.dto.FollowDTO;
import com.master.flow.model.dto.PostInfoDTO;
import com.master.flow.model.dto.UserDTO;
import com.master.flow.model.vo.*;
import com.master.flow.follow.util.KoreanStringUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class FollowService {
    @Autowired
    private ConvertToInitialsFromNameList ciList;

    @Autowired
    private KoreanStringUtil krUtil;

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
        return Follow.builder()
                .followingUser(followingUser)
                .followerUser(followerUser)
                .build();
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

    private BooleanBuilder followBuilder(String key) {
        QUser qUser = QUser.user;  // QueryDSL로 생성된 QUser 객체

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (key != null && !key.trim().isEmpty()) {
            if (krUtil.isKoreanConsonant(key) == 0) {
                booleanBuilder.and(qUser.userEmail.contains(key).or(qUser.userNickname.contains(key)));
            }
            return booleanBuilder;
        }
        return booleanBuilder;
    }

    private List<User> followingUserList(BooleanBuilder booleanBuilder, int code) {
        QUser qUser = QUser.user;  // QueryDSL로 생성된 QUser 객체
        QFollow qFollow = QFollow.follow;
        List<User> users;
        if (booleanBuilder.hasValue()) {  // booleanBuilder에 조건이 있을 때만 포함
            users = queryFactory
                    .select(qUser)
                    .from(qFollow)
                    .join(qUser).on(qFollow.followerUser.userCode.eq(qUser.userCode))
                    .where(qFollow.followingUser.userCode.eq(code)
                            .and(booleanBuilder))
                    .fetch();
        } else {  // key 조건이 없을 때는 기본 조건으로만 조회
            users = queryFactory
                    .select(qUser)
                    .from(qFollow)
                    .join(qUser).on(qFollow.followerUser.userCode.eq(qUser.userCode))
                    .where(qFollow.followingUser.userCode.eq(code))  // 기본 조건만 적용
                    .fetch();
        }
        return users;
    }

    private List<User> followerUserList(BooleanBuilder booleanBuilder, int code) {
        QUser qUser = QUser.user;  // QueryDSL로 생성된 QUser 객체
        QFollow qFollow = QFollow.follow;
        List<User> users;
        if (booleanBuilder.hasValue()) {  // booleanBuilder에 조건이 있을 때만 포함
            users = queryFactory
                    .select(qUser)
                    .from(qFollow)
                    .join(qUser).on(qFollow.followingUser.userCode.eq(qUser.userCode))
                    .where(qFollow.followerUser.userCode.eq(code)
                            .and(booleanBuilder))
                    .fetch();
        } else {  // key 조건이 없을 때는 기본 조건으로만 조회
            users = queryFactory
                    .select(qUser)
                    .from(qFollow)
                    .join(qUser).on(qFollow.followingUser.userCode.eq(qUser.userCode))
                    .where(qFollow.followerUser.userCode.eq(code))  // 기본 조건만 적용
                    .fetch();
        }
        return users;
    }
    private List<User> filteredUsers(String key, int code) {
        BooleanBuilder followFilter = followBuilder(key);
        return followingUserList(followFilter, code);
    }
    private List<User> filteredUsers2 (String key, int code) {
        BooleanBuilder followFilter = followBuilder(key);
        return followerUserList(followFilter,code);
    }
    private List<UserDTO> initialUserDTOList(List<User> initialSearchUser, int code) {
        return initialSearchUser.stream()
                .map(user -> {
                    boolean logic = checkLogic(code, user.getUserCode());
                    return new UserDTO(user, logic);
                })
                .toList();
    }
    private void containsKeyword(int i, List<String> nickNameList, List<User> filteredUsers, List<User> initialSearchUser) {
            String matchingName = nickNameList.get(i);
            filteredUsers.stream()
                    .filter(user -> user.getUserNickname().contains(matchingName)) // 닉네임 일치 여부 확인
                    .findFirst()
                    .ifPresent(initialSearchUser::add);
    }

    private void initialSearchUser(List<User> filteredUsers, int num, String keyword, String keywordOrNull, List<String> nickNameList, List<User> initialSearchUser) {
        List<String> userNickNameList = ciList.convertToInitialsFromName(filteredUsers, num, keywordOrNull);
        for (int i = 0; i < userNickNameList.size(); i++) {
            if (userNickNameList.get(i).contains(keyword)) {
                containsKeyword(i, nickNameList, filteredUsers, initialSearchUser);
            }
        }
    }
    private void finalConsonantSearchCompletedUser(List<User> filteredUsers, String keyword, List<String> nickNameList, List<User> initialSearchUser, String filteringChar) {
        List<String> userNickNameList = ciList.convertToInitialsFromName(filteredUsers, 3, keyword);
        for (int i = 0; i < filteredUsers.size(); i++) {
            if (filteredUsers.get(i).getUserNickname().contains(keyword)) {
                containsKeyword(i, nickNameList, filteredUsers, initialSearchUser);
            } else if (userNickNameList.get(i).contains(filteringChar) || userNickNameList.get(i).contains(keyword)) {
                containsKeyword(i, nickNameList, filteredUsers, initialSearchUser);
            }
        }
    }
    private void doubleDigitSearchCompletedUser(List<User> filteredUsers, String keyword, List<String> nickNameList, List<User> initialSearchUser) {
        List<String> userNickNameList = ciList.convertToInitialsFromName(filteredUsers, 3, keyword);
        for (int i = 0; i < filteredUsers.size(); i++) {
            if (filteredUsers.get(i).getUserNickname().contains(keyword)) {
                containsKeyword(i, nickNameList, filteredUsers, initialSearchUser);
            } else if (userNickNameList.get(i).contains(keyword) || (
                    userNickNameList.get(i).contains(String.valueOf(keyword.charAt(0)))
                            && userNickNameList.get(i).contains(String.valueOf(krUtil.sliceKorean(String.valueOf(keyword.charAt(keyword.length()-1))).charAt(0))))) {
                containsKeyword(i, nickNameList, filteredUsers, initialSearchUser);
            }
        }
    }
    private FollowDTO switchOfFollowDto(List<User> filteredUsers, String keyword, List<String> nickNameList, List<User> initialSearchUser, int code) {
        switch (krUtil.isKoreanConsonant(keyword)) {
            case 1: {
                // keyornull = null, keyword는 keyword num=1
                initialSearchUser(filteredUsers, 1, keyword, null, nickNameList, initialSearchUser);
                List<UserDTO> initialUserDTOList = initialUserDTOList(initialSearchUser, code);
                return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
            }
            case 2: {
                // keyornull = null num=2 keyword=keyword
                initialSearchUser(filteredUsers, 2,keyword,null,nickNameList,initialSearchUser);
                List<UserDTO> initialUserDTOList = initialUserDTOList(initialSearchUser, code);
                return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
            }
            case 3: {
                // keyornull = keyword num=2 keyword=keyword
                initialSearchUser(filteredUsers, 3, keyword, keyword, nickNameList,initialSearchUser);
                List<UserDTO> initialUserDTOList = initialUserDTOList(initialSearchUser, code);
                return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
            }
            case 4: {
                finalConsonantSearchCompletedUser(filteredUsers, keyword, nickNameList, initialSearchUser, String.valueOf(krUtil.sliceKorean(keyword).charAt(0)));
                List<UserDTO> initialUserDTOList = initialUserDTOList(initialSearchUser, code);
                return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
            }
            case 5 : {
                doubleDigitSearchCompletedUser(filteredUsers, keyword, nickNameList, initialSearchUser);
                List<UserDTO> initialUserDTOList = initialUserDTOList(initialSearchUser, code);
                return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
            }
            default: {
                List<UserDTO> userDTOList = initialUserDTOList(filteredUsers, code);
                return new FollowDTO(userDTOList.size(), userDTOList);
            }
        }
    }
    //내가 팔로우한 인간들의 수와 인간들 전체 목록 dto발사
    public FollowDTO viewMyFollower(int followingUserCode, String key) {
        if(key != null) {
            key=key.trim();
        }
        List<User> filteredUsers = filteredUsers(key, followingUserCode);
        List<String> nickNameList = ciList.nickNameList(filteredUsers);
        List<User> initialSearchUser = new ArrayList<>();

        String keyword = key;
        return switchOfFollowDto(filteredUsers, keyword, nickNameList, initialSearchUser, followingUserCode);
    }
    //위랑 반대
    public FollowDTO followMeUsers (int followerUserCode, String key) {
        if(key != null) {
            key=key.trim();
        }
        List<User> filteredUsers = filteredUsers2(key, followerUserCode);
        List<String> nickNameList = ciList.nickNameList(filteredUsers);
        List<User> initialSearchUser = new ArrayList<>();

        String keyword = key;
        return switchOfFollowDto(filteredUsers, keyword, nickNameList, initialSearchUser, followerUserCode);
    }

    // 내가 팔로우하는 유저의 게시글 조회
    public Page<PostInfoDTO> getPostsFromFollowingUsers(int userCode, Pageable pageable, BooleanBuilder builder) {
        // 팔로우한 유저의 userCode 가져오기
        List<Follow> followers = followDAO.findAllByFollowingUser_UserCode(userCode);
        List<Integer> followerUserCodes = followers.stream()
                .map(follow -> follow.getFollowerUser().getUserCode())
                .collect(Collectors.toList());

        // 팔로우한 유저의 게시물 중에서 postType이 'vote'인 게시물을 제외
        // BooleanBuilder를 사용하여 조건을 추가
        Predicate predicate = builder.and(QPost.post.user.userCode.in(followerUserCodes));

        // QuerydslPredicateExecutor를 사용하여 조건을 적용한 페이지 조회
        Page<Post> posts = postDAO.findAll(predicate, pageable);

        // Post를 PostInfoDTO로 변환
        return posts.map(post -> {
            List<PostImg> postImgs = postImgDAO.findByPost_PostCode(post.getPostCode());
            return new PostInfoDTO(post, 0, 0, postImgs);
        });
    }
    // 추천 팔로워를 해봅시다...
//    public
}
