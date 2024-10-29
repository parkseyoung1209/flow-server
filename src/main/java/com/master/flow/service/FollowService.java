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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            if (isKoreanConsonant(key) == 0) {
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
                .map(User::getUserNickname)
                .toList();
    }
    public List<String> convertToInitialsFromName(List<User> users, int num, String key) {
        System.out.println(num);
        List<String> userNickNameList = new ArrayList<>();
        for(User user : users) {
            StringBuilder initials = new StringBuilder();
            int keyIndex = 0;
            for (char ch : user.getUserNickname().toCharArray()) {
                if (ch >= 0xAC00 && ch <= 0xD7A3 && num == 1) { // 한글이냐 여부
                    int unicode = ch - 0xAC00;
                    int initialIndex = unicode / (21 * 28);
                    char initialChar = INITIALS[initialIndex];  // 초성 배열에서 가져오기
                    initials.append(initialChar);
                } else if(ch >= 0xAC00 && ch <= 0xD7A3 && num == 2) {
                    int unicode = ch - 0xAC00;
                    int initialIndex = unicode / (21 * 28); // 초성 인덱스 추출
                    int medialIndex = (unicode % (21 * 28)) / 28; // 중성 인덱스 추출

                    char combinedChar = (char) (0xAC00 + (initialIndex * 21 * 28) + (medialIndex * 28));
                    initials.append(combinedChar);
                } else if(ch >= 0xAC00 && ch <= 0xD7A3 && num == 3) {
                    int unicode = ch - 0xAC00;
                    int initialIndex = unicode / (21 * 28);
                    char initialChar = INITIALS[initialIndex];
                    if (key != null && !key.trim().isEmpty()) {
                        if (keyIndex < key.length()) {
                            char keyCh = key.charAt(keyIndex);

                            // 키가 온전한 한글 음절인 경우
                            if (keyCh >= 0xAC00 && keyCh <= 0xD7A3 && keyCh == ch) {
                                initials.append(ch);
                                keyIndex++;
                            }
                            // 키가 초성인 경우, 유저 닉네임의 초성과 비교
                            else if ((keyCh >= 0x3131 && keyCh <= 0x314E) && keyCh == initialChar) {
                                initials.append(initialChar);
                                keyIndex++;
                            }
                            // 키와 매칭되지 않는 경우, 유저의 초성만 추가
                            else {
                                initials.append(initialChar);
                            }
                        } else {
                            // 키의 길이를 넘어섰을 때, 남은 닉네임의 글자는 초성만 추가
                            initials.append(initialChar);
                        }
                    } else {
                        initials.append(ch);  // 한글이 아닌 경우 그대로 추가
                    }
                }
            }
            userNickNameList.add(initials.toString());
        }

        return userNickNameList; // 한글 닉네임의 초성 문자열이 나옴 홍길동-> ㅎㄱㄷ || 홍ㄱㄷ
    }

    // 초성 배열 (유니코드 기준)
    private static final char[] INITIALS = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ',
            'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    private int isKoreanConsonant(String key) {
        if (key == null || key.trim().isEmpty()) {
            return 0;
        }
        for(int i=0; i<key.length(); i++) {
            if (key.length() > 1 && (key.charAt(i) >= 0x3131 && key.charAt(i) <= 0x314E)) return 3;
        }
        if(key.length() == 1 && (key.charAt(0) >= 0x3131 && key.charAt(0) <= 0x314E)) {
            return 1;
        }
        if (key.charAt(0) >= 0xAC00 && key.charAt(0) <= 0xD7A3) {
            int unicode = key.charAt(0) - 0xAC00;
            int finalIndex = unicode % 28;
                if (finalIndex == 0) {
                    return 2;
                }
        }
        return 0;
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

        switch (isKoreanConsonant(key)) {
            case 1 : {
                List<User> initialSearchUser = new ArrayList<>();
                List<String> userNickNameList = convertToInitialsFromName(filteredUsers, 1, null);
                List<String> nickNameList = nickNameList(filteredUsers); // 원본닉네임 리스트// 초성 리스트
                for(int i = 0; i < userNickNameList.size(); i++) {
                    if(userNickNameList.get(i).contains(key)) {
                        String matchingName = nickNameList.get(i);
                        filteredUsers.stream()
                                .filter(user -> user.getUserNickname().contains(matchingName)) // 닉네임 일치 여부 확인
                                .findFirst()
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
            case 2 : {
                List<User> initialSearchUser = new ArrayList<>();
                List<String> userNickNameList = convertToInitialsFromName(filteredUsers, 2, null);
                List<String> nickNameList = nickNameList(filteredUsers); // 원본닉네임 리스트// 초성 리스트
                for(int i = 0; i < userNickNameList.size(); i++) {
                    if(userNickNameList.get(i).contains(key)) {
                        String matchingName = nickNameList.get(i);
                        filteredUsers.stream()
                                .filter(user -> user.getUserNickname().contains(matchingName)) // 닉네임 일치 여부 확인
                                .findFirst()
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
            case 3 : {
                List<User> initialSearchUser = new ArrayList<>();
                List<String> userNickNameList = convertToInitialsFromName(filteredUsers, 3, key);
                List<String> nickNameList = nickNameList(filteredUsers); // 원본닉네임 리스트
                for(int i = 0; i < userNickNameList.size(); i++) {
                    if(userNickNameList.get(i).contains(key)) {
                        String matchingName = nickNameList.get(i);
                        filteredUsers.stream()
                                .filter(user -> user.getUserNickname().contains(matchingName)) // 닉네임 일치 여부 확인
                                .findFirst()
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
            default: {
//                 User 리스트를 UserDTO 리스트로 변환
                List<UserDTO> userDTOList = filteredUsers.stream()
                        .map(user -> {
                            boolean logic = checkLogic(followingUserCode, user.getUserCode());
                            return new UserDTO(user, logic);
                        })
                        .collect(Collectors.toList());

                // FollowDTO로 변환하여 반환
                return new FollowDTO(userDTOList.size(), userDTOList);
            }
        }
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
        switch (isKoreanConsonant(key)) {
            case 1 : {
                List<User> initialSearchUser = new ArrayList<>();
                List<String> userNickNameList = convertToInitialsFromName(filteredUsers, 1, null);
                List<String> nickNameList = nickNameList(filteredUsers); // 원본닉네임 리스트// 초성 리스트
                for(int i = 0; i < userNickNameList.size(); i++) {
                    if(userNickNameList.get(i).contains(key)) {
                        String matchingName = nickNameList.get(i);
                        filteredUsers.stream()
                                .filter(user -> user.getUserNickname().contains(matchingName)) // 닉네임 일치 여부 확인
                                .findFirst()
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
            case 2 : {
                List<User> initialSearchUser = new ArrayList<>();
                List<String> userNickNameList = convertToInitialsFromName(filteredUsers, 2, null);
                List<String> nickNameList = nickNameList(filteredUsers); // 원본닉네임 리스트// 초성 리스트
                for(int i = 0; i < userNickNameList.size(); i++) {
                    if(userNickNameList.get(i).contains(key)) {
                        String matchingName = nickNameList.get(i);
                        filteredUsers.stream()
                                .filter(user -> user.getUserNickname().contains(matchingName)) // 닉네임 일치 여부 확인
                                .findFirst()
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
            case 3 : {
                List<User> initialSearchUser = new ArrayList<>();
                List<String> userNickNameList = convertToInitialsFromName(filteredUsers, 3, key);
                List<String> nickNameList = nickNameList(filteredUsers); // 원본닉네임 리스트// 초성 리스트
                for(int i = 0; i < userNickNameList.size(); i++) {
                    if(userNickNameList.get(i).contains(key)) {
                        String matchingName = nickNameList.get(i);
                        filteredUsers.stream()
                                .filter(user -> user.getUserNickname().contains(matchingName)) // 닉네임 일치 여부 확인
                                .findFirst()
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
            default: {
//                 User 리스트를 UserDTO 리스트로 변환
                List<UserDTO> userDTOList = filteredUsers.stream()
                        .map(user -> {
                            boolean logic = checkLogic(followerUserCode, user.getUserCode());
                            return new UserDTO(user, logic);
                        })
                        .collect(Collectors.toList());

                // FollowDTO로 변환하여 반환
                return new FollowDTO(userDTOList.size(), userDTOList);
            }
        }
    }

    // 내가 팔로우하는 유저의 게시글 조회
    public Page<PostInfoDTO> getPostsFromFollowingUsers(int userCode, Pageable pageable) {
        List<Follow> followers = followDAO.findAllByFollowingUser_UserCode(userCode);
        List<Integer> followerUserCodes = followers.stream()
                .map(follow -> follow.getFollowerUser().getUserCode())
                .collect(Collectors.toList());

        Page<Post> posts = postDAO.findByUser_UserCodeIn(followerUserCodes, pageable);

        return posts.map(post -> {
            List<PostImg> postImgs = postImgDAO.findByPost_PostCode(post.getPostCode());
            return new PostInfoDTO(post, 0, 0, postImgs);
        });
    }
    // 추천 팔로워를 해봅시다...
//    public
}
