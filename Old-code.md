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
