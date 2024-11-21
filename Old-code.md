``` java
import com.master.flow.model.dao.FollowDAO;
import com.master.flow.model.vo.Follow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class FollowService {
    // 추가 및 중복방지 로직

    // 비교를 위한 모든 팔로우 관계가 있는 테이블 가져오기
    public ArrayList<Follow> viewAllFollowList() {
        return (ArrayList<Follow>) followDAO.findAll();
    }

    // 유저가 새로 넣은 데이터를 바탕으로 만든 임의 관계도
    private String getUserRelative(Follow value) {
        return value.getFollowingUser() + "_" + value.getFollowerUser();
    }
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
}
```

```java
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

        BooleanExpression expression = qFollow.followingUser.userCode.eq(followingUserCode);
        booleanBuilder.and(expression);

        List<Follow> follows = (List<Follow>) followDAO.findAll(booleanBuilder);

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
```
``` java
 public BooleanBuilder followBuilder(String key, List<User> list) {
        QUser qUser = QUser.user;  // QueryDSL로 생성된 QUser 객체
        QFollow qFollow = QFollow.follow;

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
```

```java
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
```

```java
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
```
