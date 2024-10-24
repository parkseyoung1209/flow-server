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


    public BooleanBuilder followBuilder(String key, List<User> list) {
        QUser qUser = QUser.user;  // QueryDSL로 생성된 QUser 객체

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (key != null && !key.trim().isEmpty()) {
            if (!isKoreanConsonant(key)) {
                booleanBuilder.and(qUser.userEmail.contains(key).or(qUser.userNickname.contains(key)));
            }
            return booleanBuilder;
        }
        return booleanBuilder;
    }

    public List<User> followingUserList(BooleanBuilder booleanBuilder, int code) {
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

    public List<User> followerUserList(BooleanBuilder booleanBuilder, int code) {
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
    public List<String> nickNameList (List<User> users) {
        return users.stream()
                .map(user -> user.getUserNickname())
                .toList();
    }
    public List<String> convertToInitialsFromName(List<User> users) {
        List<String> userNickNameList = new ArrayList<>();
        for(User user : users) {
            StringBuilder initials = new StringBuilder();
            for (char ch : user.getUserNickname().toCharArray()) {
                if (ch >= 0xAC00 && ch <= 0xD7A3) {  // 한글 유니코드 범위
                    int unicode = ch - 0xAC00;
                    int initialIndex = unicode / (21 * 28);
                    char initialChar = INITIALS[initialIndex];  // 초성 배열에서 가져오기
                    initials.append(initialChar);
                } else {
                    initials.append(ch);  // 한글이 아닌 경우 그대로 추가
                }
            }
            userNickNameList.add(initials.toString());
        }
        return userNickNameList; // 한글 닉네임의 초성 문자열이 나옴 홍길동-> ㅎㄱㄷ
    }

    // 초성 배열 (유니코드 기준)
    private static final char[] INITIALS = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ',
            'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };
    private boolean isKoreanConsonant(String key) {
        // key가 한글 자음인지 확인 (한글 자음 유니코드 범위: ㄱ ~ ㅎ)
        if (key == null || key.trim().isEmpty()) {
            return false;  // key가 null이거나 빈 문자열일 때 false 반환
        }
        if(key.length() <2 ) {
            System.out.println("단일문자라면" + key.charAt(0));
        } else {
            System.out.println("첫번째는" + key.charAt(0));
            System.out.println("두번째는" + key.charAt(1));
        }
        // key가 한글 자음인지 확인 (한글 자음 유니코드 범위: ㄱ ~ ㅎ)
        return key.length() >= 1 && (key.charAt(0) >= 0x3131 && key.charAt(0) <= 0xD7A3);
    }

    //내가 팔로우한 인간들의 수와 인간들 전체 목록 dto발사
    public FollowDTO viewMyFollower(int followingUserCode, String key) {
        QUser qUser = QUser.user;  // QueryDSL로 생성된 QUser 객체

        // 먼저 유저 리스트를 가져옴 (필터링에 사용)
        List<User> allUsers = queryFactory
                .selectFrom(qUser)
                .where(qUser.userHeight.isNotNull())
                .fetch(); // 모든 유저 목록을 가져옴

        // 필터링 조건을 생성
        BooleanBuilder followFilter = followBuilder(key, allUsers);
        // key와 유저 리스트로 필터링 조건 생성
        // 조건에 맞는 팔로우 유저 리스트 가져오기
        List<User> filteredUsers = followingUserList(followFilter, followingUserCode);
        if (isKoreanConsonant(key)) {
            List<User> initialSearchUser = new ArrayList<>();
            // key가 자음이면 초성으로 검색
            List<String> userNickNameList = convertToInitialsFromName(filteredUsers);
            List<String> nickNameList = nickNameList(filteredUsers); // 원본닉네임 리스트// 초성 리스트
            for(int i = 0; i < userNickNameList.size(); i++) {
                if(userNickNameList.get(i).contains(key)) {
                    String matchingName = nickNameList.get(i);
                    filteredUsers.stream()
                            .filter(user -> user.getUserNickname().contains(matchingName)) // 닉네임 일치 여부 확인
                            .findFirst()  // 첫 번째 일치하는 유저 가져오기
                            .ifPresent(initialSearchUser::add);
                }
            }
            List<UserDTO> initialUserDTOList = initialSearchUser.stream()
                    .map(user -> {
                        boolean logic = checkLogic(followingUserCode, user.getUserCode());
                        return new UserDTO(user,logic);
                    })
                    .toList();
            return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
        }

        // User 리스트를 UserDTO 리스트로 변환
        List<UserDTO> userDTOList = filteredUsers.stream()
                .map(user -> {
                    boolean logic = checkLogic(followingUserCode, user.getUserCode());
                    return new UserDTO(user, logic);
                })
                .collect(Collectors.toList());

        // FollowDTO로 변환하여 반환
        return new FollowDTO(userDTOList.size(), userDTOList);
    }
    //위랑 반대
    public FollowDTO followMeUsers (int followerUserCode, String key) {
        QUser qUser = QUser.user;  // QueryDSL로 생성된 QUser 객체

        List<User> allUsers = queryFactory
                .selectFrom(qUser)
                .where(qUser.userHeight.isNotNull())
                .fetch(); // 모든 유저 목록을 가져옴
        BooleanBuilder followFilter = followBuilder(key, allUsers);

        List<User> filteredUsers = followerUserList(followFilter, followerUserCode);

        if (isKoreanConsonant(key)) {
            List<User> initialSearchUser = new ArrayList<>();
            // key가 자음이면 초성으로 검색
            List<String> userNickNameList = convertToInitialsFromName(filteredUsers);
            List<String> nickNameList = nickNameList(filteredUsers); // 원본닉네임 리스트// 초성 리스트
            for(int i = 0; i < userNickNameList.size(); i++) {
                if(userNickNameList.get(i).contains(key)) {
                    String matchingName = nickNameList.get(i);
                    filteredUsers.stream()
                            .filter(user -> user.getUserNickname().contains(matchingName)) // 닉네임 일치 여부 확인
                            .findFirst()  // 첫 번째 일치하는 유저 가져오기
                            .ifPresent(initialSearchUser::add);
                }
            }
            List<UserDTO> initialUserDTOList = initialSearchUser.stream()
                    .map(user -> {
                        boolean logic = checkLogic(followerUserCode, user.getUserCode());
                        return new UserDTO(user,logic);
                    })
                    .toList();
            return new FollowDTO(initialUserDTOList.size(), initialUserDTOList);
        }

        List<UserDTO> userDTOList = filteredUsers.stream()
                .map(user -> {
                    boolean logic = checkLogic(followerUserCode, user.getUserCode());
                    return new UserDTO(user, logic);
                })
                .collect(Collectors.toList());

        // FollowDTO로 변환하여 반환
        return new FollowDTO(userDTOList.size(), userDTOList);
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
