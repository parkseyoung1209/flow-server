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
    // 처음 검색어가 존재하지 않거나 공백문자열일 때 밸류없는 불리언빌더 생성을 위함
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
    // 이후 스트링 문자열이 온전히 포함된 리스트는 그대로 가져오고, 검색어가 포함되지 않으면 where 조건에만 맞는 리스트 가져오기
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
    // 위와 같음
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
    //위에 만들어진 불리언빌더와 팔로잉/팔로우리스트를 조합한 유저 객체 리스트
    private List<User> filteredUsers(String key, int code) {
        BooleanBuilder followFilter = followBuilder(key);
        return followingUserList(followFilter, code);
    }
    private List<User> filteredUsers2 (String key, int code) {
        BooleanBuilder followFilter = followBuilder(key);
        return followerUserList(followFilter,code);
    }

    //헬퍼 클래스의 convertToInitialsFromName를 기반으로 검색어에 매칭되는 닉네임들을 가진 filtereduser의 유저들만 추출후 initialSearchUser 리스트에 넣는 과정을 개별 메서드들로 분리
    //containsKeyword ~ switchOfFollow까지 한 과정...
    private void containsKeyword(int i, List<String> nickNameList, List<User> filteredUsers, List<User> initialSearchUser) {
            String matchingName = nickNameList.get(i);
            filteredUsers.stream()
                    .filter(user -> user.getUserNickname().contains(matchingName)) // 닉네임 일치 여부 확인
                    .findFirst()
                    .ifPresent(initialSearchUser::add);
            // 필터드 유저는 검색 입력값이 별도의 처리 없이 포함 가능하게끔 입력했다면 해당하는 유저만 가져오고, 아닌 경우엔 기본 WHERE 조건만 있는 팔로워/팔로위 리스트를 가져온다.
            // 여기서 밑에 헬퍼클래스로 재구성한 닉네임 리스트와 검색어가 일치하는 순간에만 위 스트림 문을 실행한다.
            // 헬퍼클래스의 인덱스와 닉네임리스트의 인덱스가 일치하기 때문에 가능하다
    }

    private void initialSearchUser(List<User> filteredUsers, int num, String keyword, String keywordOrNull, List<String> nickNameList, List<User> initialSearchUser) {
        List<String> userNickNameList = ciList.convertToInitialsFromName(filteredUsers, num, keywordOrNull);
        for (int i = 0; i < userNickNameList.size(); i++) {
            if (userNickNameList.get(i).contains(keyword)) {
                containsKeyword(i, nickNameList, filteredUsers, initialSearchUser); // 위에 메서드 넣기
            }
        }
    }
    private void finalConsonantSearchCompletedUser(List<User> filteredUsers, String keyword, List<String> nickNameList, List<User> initialSearchUser, String filteringChar) {
        List<String> userNickNameList = ciList.convertToInitialsFromName(filteredUsers, 3, keyword);
        for (int i = 0; i < filteredUsers.size(); i++) {
            if (filteredUsers.get(i).getUserNickname().contains(keyword)) {
                containsKeyword(i, nickNameList, filteredUsers, initialSearchUser);
            } else if (userNickNameList.get(i).contains(filteringChar) || userNickNameList.get(i).contains(keyword)) {
                // 종성이 있는 경우를 filteringChar와 ciList.convertToInitialsFromName로 처리한다.
                containsKeyword(i, nickNameList, filteredUsers, initialSearchUser);
            }
        }
    }
    private void doubleDigitSearchCompletedUser(List<User> filteredUsers, String keyword, List<String> nickNameList, List<User> initialSearchUser) {
        List<String> userNickNameList = ciList.convertToInitialsFromName(filteredUsers, 3, keyword);
        for (int i = 0; i < filteredUsers.size(); i++) {
            if (filteredUsers.get(i).getUserNickname().contains(keyword)) {
                containsKeyword(i, nickNameList, filteredUsers, initialSearchUser);
                // else if의 두번째 조건은 2글자 이상의 한글 문자열에서 첫글자가 같고, 끝 글자를 koreanStringUtil의 slicekorean으로 자른 문자가 포함되는 경우이다.
            } else if (userNickNameList.get(i).contains(keyword) || (
                    userNickNameList.get(i).contains(String.valueOf(keyword.charAt(0)))
                            && userNickNameList.get(i).contains(String.valueOf(krUtil.sliceKorean(String.valueOf(keyword.charAt(keyword.length()-1))).charAt(0))))) {
                containsKeyword(i, nickNameList, filteredUsers, initialSearchUser);
            }
        }
    }
    private List<UserDTO> initialUserDTOList(List<User> initialSearchUser, int code) {
        return initialSearchUser.stream()
                .map(user -> {
                    boolean logic = checkLogic(code, user.getUserCode());
                    return new UserDTO(user, logic);
                })
                .toList();
    }
    private FollowDTO switchOfFollowDto(List<User> filteredUsers, String keyword, List<String> nickNameList, List<User> initialSearchUser, int code) {
        switch (krUtil.isKoreanConsonant(keyword)) {
            case 1: {
                // 문자/문자열이 초성으로만 조합되었을 때 유저의 닉네임을 초성으로만 재구축
                initialSearchUser(filteredUsers, 1, keyword, null, nickNameList, initialSearchUser);
                List<UserDTO> initialUserDTOList = initialUserDTOList(initialSearchUser, code);
                return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
            }
            case 2: {
                // 문자/문자열이 유저들의 닉네임의 초성+중성과 일치하는 경우 재구축
                initialSearchUser(filteredUsers, 2,keyword,null,nickNameList,initialSearchUser);
                List<UserDTO> initialUserDTOList = initialUserDTOList(initialSearchUser, code);
                return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
            }
            case 3: {
                // 문자열의 다음 문자가 초성일 경우 리스트 재구성
                // 예를 들어 검색어가 '신ㄱ' 이고 풀네임은 '신고테스트' 일 경우 '신ㄱㅌㅅㅌ'로 변환
                initialSearchUser(filteredUsers, 3, keyword, keyword, nickNameList,initialSearchUser);
                List<UserDTO> initialUserDTOList = initialUserDTOList(initialSearchUser, code);
                return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
            }
            case 4: {
                // 단일 문자가 종성이 있을 경우 slicekorean 메서드로 종성 해체 후 리스트와 비교
                finalConsonantSearchCompletedUser(filteredUsers, keyword, nickNameList, initialSearchUser, String.valueOf(krUtil.sliceKorean(keyword).charAt(0)));
                List<UserDTO> initialUserDTOList = initialUserDTOList(initialSearchUser, code);
                return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
            }
            case 5 : {
                // 문자열의 끝자리가 초성+중성 이상일 경우 처리...
                doubleDigitSearchCompletedUser(filteredUsers, keyword, nickNameList, initialSearchUser);
                List<UserDTO> initialUserDTOList = initialUserDTOList(initialSearchUser, code);
                return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
            }
            default: {
                // 검색어가 없다면 전체 리스트 그대로
                List<UserDTO> userDTOList = initialUserDTOList(filteredUsers, code);
                return new FollowDTO(userDTOList.size(), userDTOList);
            }
        }
    }
    // 최종적으로 걸러진 유저리스트와 팔로우 여부를 followDTO로 묶어서 보내기
    public FollowDTO viewMyFollower(int followingUserCode, String keyword) {
        if(keyword != null) {
            keyword=keyword.trim();
        }
        List<User> filteredUsers = filteredUsers(keyword, followingUserCode);
        List<String> nickNameList = ciList.nickNameList(filteredUsers);
        List<User> initialSearchUser = new ArrayList<>();

        return switchOfFollowDto(filteredUsers, keyword, nickNameList, initialSearchUser, followingUserCode);
    }
    // 위랑 같음
    public FollowDTO followMeUsers (int followerUserCode, String keyword) {
        if(keyword != null) {
            keyword = keyword.trim();
        }
        List<User> filteredUsers = filteredUsers2(keyword, followerUserCode);
        List<String> nickNameList = ciList.nickNameList(filteredUsers);
        List<User> initialSearchUser = new ArrayList<>();

        return switchOfFollowDto(filteredUsers, keyword, nickNameList, initialSearchUser, followerUserCode);
    }

    // 내가 팔로우하는 유저의 게시글 조회
    public Page<PostInfoDTO> getPostsFromFollowingUsers(int userCode, Pageable pageable, BooleanBuilder builder) {

        List<Follow> followers = followDAO.findAllByFollowingUser_UserCode(userCode);
        List<Integer> followerUserCodes = followers.stream()
                .map(follow -> follow.getFollowerUser().getUserCode())
                .collect(Collectors.toList());

        QPost qPost = QPost.post;

        // 팔로우한 유저의 게시물 중에서 postType이 'vote'인 게시물을 제외
        builder.and(qPost.user.userCode.in(followerUserCodes))
                .and(qPost.postType.ne("vote"))
                .and(qPost.postPublicYn.eq("Y"));

        Page<Post> posts = postDAO.findAll(builder, pageable);

        return posts.map(post -> {
            List<PostImg> postImgs = postImgDAO.findByPost_PostCode(post.getPostCode());
            return new PostInfoDTO(post, 0, 0, postImgs); // PostInfoDTO로 변환
        });
    }
}
