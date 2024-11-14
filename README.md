# 팔로우 기능 관련 설명

## 1. 데이터베이스 설계
팔로우 기능을 데이터베이스 관점에서 보면, 결국 두 개의 주요 컬럼만 필요하다고 생각했습니다.

- **팔로워**: 팔로우 / 언팔로우 하는 사용자
- **팔로위**: 팔로우 / 언팔로우 당하는 사용자

이 두 컬럼을 통해 중복을 방지하고 관계의 고유성을 증명할 수 있다면, 팔로우 기능과 관련된 모든 기능을 구현할 수 있습니다.

### (1) 초기 구현
```sql
CREATE TABLE FOLLOW (
    FOLLOWING_CODE INT AUTO_INCREMENT PRIMARY KEY,
    FOLLOWING_USER INT,
    FOLLOWER_USER INT
)
```
- 데이터베이스 종류로는 `MySQL`을 사용하였습니다.
   
- 관계의 고유성은 **프라이머리 키로 생성된 `FOLLOWING_CODE`**를 통해 구현하였고, 중복 팔로우/언팔로우 및 자기 자신을 팔로우/언팔로우하는 오류는 서버 코드에서 처리할 계획으로, 데이터베이스는 기본적인 틀만 잡았습니다.
  
- 또한 `FOLLOWING_USER`와 `FOLLOWER_USER` 컬럼에 외래 키를 따로 지정하지 않고, 단순히 유저 코드를 받는 형태로 설계하였습니다.
  
    #### 문제점
    - 서버에서 중복 처리를 하다 보니, 데이터베이스의 고유성 제약이나 인덱스 기능을 제대로 활용하지 못해 데이터베이스 성능을 떨어뜨리고, **불필요한 서버 비용**이 발생하는 문제가 있었습니다.
    
    - 설계 후에 깨달은 문제는, 저는 기능상 다른 테이블과 `JOIN`할 필요가 없었지만, 팀원들이 팔로우 테이블을 사용할 때 외래 키 참조를 생략하여 기능적인 오류가 발생했다는 점입니다.

### (2) 보완된 코드
```sql
CREATE TABLE FOLLOW(
  FOLLOWING_USER INT,
  FOLLOWER_USER INT,
  PRIMARY KEY(FOLLOWING_USER, FOLLOWER_USER),
  FOREIGN KEY(FOLLOWING_USER) REFERENCES USER(USER_CODE) ON DELETE CASCADE, 
  FOREIGN KEY(FOLLOWER_USER) REFERENCES USER(USER_CODE) ON DELETE CASCADE,
  CHECK (FOLLOWING_USER <> FOLLOWER_USER)
)
```
- **`FOLLOWING_CODE` 제거** : 이전 구현에서 사용했던 `FOLLOWING_CODE` 컬럼을 제거하였습니다.<br>
이 컬럼은 필요 이상의 중복 정보를 갖고 있었으며, 복합 키를 통해 관계의 고유성을 확보할 수 있기 때문에 불필요하다고 판단되었습니다.

- **복합 고유키 사용**: `FOLLOWING_USER`와 `FOLLOWER_USER`를 **`복합 키(Primary Key)`**로 설정하여, 사용자 간의 팔로우 관계 자체에 고유성을 부여하였습니다.

- **외래 키 및 `ON DELETE CASCADE`**: 두 컬럼 모두 `사용자 테이블(USER)`의 고유 키와 연결되었습니다. `ON DELETE CASCADE` 옵션을 통해 사용자가 삭제될 때 관련된 팔로우 관계도 자동으로 삭제되도록 처리하였습니다.

- **자기 자신을 팔로우하는 경우 방지**: `CHECK (FOLLOWING_USER <> FOLLOWER_USER)` 제약 조건을 통해 자기 자신을 팔로우하는 예외 케이스를 방지하였습니다.

## 2. 자바 서버 코드 구현
### (1) 모델 구현
- **데이터베이스와 매핑할 자바 클래스입니다.    팔로우 관계를 명확하게 표현하기 위해 `Follow` 엔티티와 복합 키를 위한 `FollowId` 클래스를 구현했습니다.**

```java

// Follow 클래스

package com.master.flow.model.vo;

import com.master.flow.model.id.FollowId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(FollowId.class)
public class Follow {
    @Id
    @ManyToOne
    @JoinColumn(name = "FOLLOWING_USER", referencedColumnName = "USER_CODE")
    private User followingUser;

    @Id
    @ManyToOne
    @JoinColumn(name = "FOLLOWER_USER", referencedColumnName = "USER_CODE")
    private User followerUser;
}
```

- `Follow` 클래스 : Follow 엔티티는 팔로우 관계를 나타내며, `@IdClass`를 사용하여 두 개의 필드를 복합 키로 설정하였습니다.
  
  * `@ManyToOne`과 `@JoinColumn`을 사용하여 `사용자(User) 테이블`과 외래 키 관계를 정의했습니다.
  
  * `followingUser`와 `followerUser`는 각각 팔로우 하는 사람과 팔로우 당하는 사람을 나타냅니다.
  
  * 복합 키를 사용하여 동일한 팔로우 관계가 중복되지 않도록 설계되었습니다.

```java

// FollowId 클래스

package com.master.flow.model.id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class FollowId implements Serializable {
    private int followingUser;
    private int followerUser;
}
```
- `FollowId` 클래스: `FollowId`는 복합 키를 구현하기 위해 사용하는 클래스입니다.
  
  * `Serializable` 인터페이스를 구현하여 `JPA`에서 복합 키로 사용할 수 있도록 하였습니다.
    
  * `followingUser`와 `followerUser` 필드는 `Follow` 엔티티와 매핑되는 키 필드입니다.

**주요 구현 설명**
- `Lombok` 사용:
  * `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Builder` 등의 `Lombok` 어노테이션을 활용하여 `getter`, `setter`, `생성자` 등을 자동으로 생성했습니다.
    
- `JPA`와의 통합:
  * `jakarta.persistence`의 `JPA 어노테이션`을 사용하여 데이터베이스와 자바 객체 간의 매핑을 간단히 구현하였습니다.
    
- 복합 키 구현:
  * 팔로우 관계는 **두 사용자의 조합에 대한 고유성**을 가지므로, 이를 표현하기 위해 `복합 키(IdClass)`를 사용했습니다. 이를 통해 팔로우/언팔로우 관계의 중복을 방지하게 됩니다.
    


### (2) JPA 인터페이스
- **팔로우 기능과 관련된 데이터 처리를 위해 `JPA` 기반으로 구현된 인터페이스입니다. 이 인터페이스는 이후 서비스와 컨트롤러에서 사용할 데이터 접근 로직을 정의하며, 팔로우 관계에 대한 `CRUD` 작업과 커스텀 쿼리를 제공합니다.**

```java
package com.master.flow.model.dao;

import com.master.flow.model.id.FollowId;
import com.master.flow.model.vo.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.HashSet;
import java.util.List;

public interface FollowDAO extends JpaRepository<Follow, FollowId>, QuerydslPredicateExecutor<Follow> {
    @Query("SELECT f FROM Follow f")
    HashSet<Follow> findAllFollowSet();

    /*List<Follow> findAllByFollowingUser_UserCode(int userCode); // 다른 팀원분이 별도로 만든 기능입니다.*/
}
```
**주요 구현 설명**
1. JPA 기반의 데이터 접근 로직:

    * `FollowDAO`는 팔로우 관계를 데이터베이스에서 `CRUD(생성, 읽기, 수정, 삭제)`하기 위한 `JPA 인터페이스`입니다.
    
    * `JpaRepository<Follow, FollowId>`를 확장하여 기본적인 `CRUD` 메서드를 제공하는 인터페이스를 만들었습니다.
      
2. 커스텀 쿼리와 `QueryDSL` 사용:

    * `@Query("SELECT f FROM Follow f")`를 사용하여 모든 팔로우 관계를 `HashSet`으로 반환하는 메서드를 정의했습니다. 이 메서드는 이후 다양한 비즈니스 로직에서 팔로우 관계를 빠르게 검색하기 위해 만들었습니다.
      
    * `QuerydslPredicateExecutor<Follow>` 인터페이스를 상속하여 동적 쿼리를 작성할 수 있는 기능을 제공합니다.
  

### (3) 서비스 구현

- **팔로우에 관한 기능이 실질적으로 처리되는 비즈니스 로직들이 담긴 서비스 코드입니다.**
  
- 서비스 전체 코드는 링크에 걸어놨습니다.   <a href="https://github.com/parkseyoung1209/flow-server-mine/blob/Seyoung/src/main/java/com/master/flow/service/FollowService.java">Service 링크</a>

#### 1. 기본적인 `CREATE/DELETE` 로직

##### (1) 개요

- 클라이언트에게 받은 로그인 유저 정보와 팔로우/언팔로우의 대상이 되는 유저 정보를 받아 기능을 구현합니다.

##### (2) 해당 코드

```java
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
}
```
**주요 구현 설명**

1. **`findAllFollowSet`** 메서드
   * `FollowDAO`를 이용하여 커스텀 쿼리로 모든 팔로우 관계를 가져옵니다. `HashSet`의 특성을 이용해 특정 데이터를 효율적으로 비교하기 위해 사용하였습니다.

2. **`existFollow`** 메서드
   * `User` 테이블을 매핑하기 위해 만든 `userDAO` 인터페이스를 이용하여 각 **로그인 한 유저 코드** 와 **팔로우할 유저 코드** 에 해당하는 `User` 객체를 추출하고 이 두 객체를 `Follow` 객체의 요소로 빌드합니다.
      
3. **`checkLogic`** 메서드
   * `findAllFollowSet`으로 가져온 팔로우 테이블과 `existFollow` 메서드로 만든 `Follow 객체`를 포함여부를 판단하여 true/false를 리턴합니다.
  
4. **`addFollowRelative/unfollow`** 메서드
   * `checkLogic` 메서드로 리턴 된 true/false 값에 따라 JPA 인터페이스의 `save/delete` 메서드를 사용하여 팔로우/언팔로우를 구현했습니다.


#### 2. 팔로워/팔로위 목록 가져오기

##### (1) 개요

팔로워 및 팔로잉 목록을 불러오고, 검색어를 기준으로 특정 조건에 맞는 사용자 목록을 가져오는 기능을 구현한 서비스 코드입니다.  이 기능은 다음과 같은 주요 로직을 포함합니다.

- 특정 사용자가 팔로우하고 있는 다른 사용자 목록을 가져오기 (`followingUserList`)
  
- 특정 사용자를 팔로우하고 있는 사용자 목록을 가져오기 (`followerUserList`)

  
##### (2) 해당 코드

```java
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
    /*
.
.
팔로위 리스트도 구조 동일
*/
```


**주요 구현 설명**

1. **`followBuilder` + ` followingUserList`** 메서드
   
   * `QueryDSL`로 만든 객체 두 개를 이용하여 특정 검색 키워드가 없을 경우, 온전한 리스트를 가져오고 있을 경우라면 `followBuilder` 조건이 추가된 리스트를 가져옵니다.

#### 3.키워드로 필터링 되는 최종적인 리스트 추출 로직

##### (1) 개요
- 아래에 후술할 한글 검색 관련 유틸리티 클래스들을 기반으로 추출된 유저 정보에 팔로우/언팔로우 여부를 첨부한 `UserDTO` 객체 타입의 리스트와 전체 유저 수를 나타내는 `리스트의 사이즈`를 `FollowDTO`로 보냅니다.
- 서비스 로직 코드는 상단 링크에서 확인 부탁드립니다

##### (2) UserDTO 코드
```java
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserDTO {
    private boolean isFollowing; 
    private User user;

    public UserDTO(User user, boolean isFollowing) {
        this.user = user;
        this.isFollowing = isFollowing;
    }
}
```
- `User`객체와 팔로우 여부를 묻는 `isFollowing`으로 구성되어있습니다.

##### (3) FollowDTO 코드
```java
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class FollowDTO {
    private int countFollower;
    private List<UserDTO> Follower;
}
```
- `UserDTO` 객체들이 들어있는 리스트 `Follower`와 유저의 수에 해당하는 `countFollower`입니다.
  
     <br>
      <br>
       <br>
       
### (4) 한글 검색 관련 유틸리티 기능

- **사용자 편의성을 위해 검색 키워드가 유저의 닉네임이나 이메일과 온전히 일치하지 않아도 검색 범위에 포함되게끔 하기 위해 구성한 유틸리티들입니다.**

 
    #### (1) 검색 키워드를 바탕으로 유저의 닉네임을 재구성하는 헬퍼 클래스
  
    - <a href="https://github.com/hjyj4841/flow-server/blob/Seyoung/src/main/java/com/master/flow/follow/helperClass/ConvertToInitialsFromNameList.java">전체 코드 링크</a>
    
    - **클래스 구성**
    
        * 객체 타입의(현 로직에서는 `User 객체`) 리스트의 스트링 요소만 추출한 후 스트링 타입 리스트로 리턴하는 `nickNameList` 메서드
          
        * 하단의 유틸리티 클래스를 이용하여 `{초성 or 초성+중성 or 초성+중성+종성}` 조합의 문자/문자열에 대응하는 새로운 `nickNameList` 를 만들어주는 `convertToInitialsFromName` 메서드

 
    #### (2) 헬퍼 클래스 맞춤 한글 문자열 처리 유틸리티 클래스

    - <a href="https://github.com/hjyj4841/flow-server/blob/Seyoung/src/main/java/com/master/flow/follow/util/KoreanStringUtil.java"> 전체 코드 링크</a>
    
    - **클래스 구성**
    
        * 한글 초성을 모아놓은 `INITIALS` 문자열 배열
          
        * 종성을 모아놓은 문자열 `FINAL_CONSONANTS` 배열
    
        * 복합종성을 키값으로 가지고 두 개를 나눈 문자열을 밸류로 가지는 `COMPLEX_CONSONANTS` 맵
          
        * 문자/문자열이 한글인지 판독하고 한글이라면 `{초성 or 초성+중성 or 초성+중성+종성}` 인지 확인하는 `isKoreanConsonant` 메서드
          
        * 종성이 있는 문자/문자열의 끝 문자의 종성을 제거하거나 복합 종성을 단일종성 두 개로 나누는 `sliceKorean` 메서드
          <br>
          <br>
          <br>
### (5) 컨트롤러 구현

- **클라이언트와 서버간의 상호작용을 위한 컨트롤러입니다.**
  
- <a href="https://github.com/hjyj4841/flow-server/blob/Seyoung/src/main/java/com/master/flow/controller/FollowController.java"> 전체 코드 링크</a>
  <br>
  <br>
  
**주요 구현 설명**
    
1. **`addFollowRelative/unFollow`** 메서드
   * `클라이언트`가 보낸 데이터를 매핑 후 서비스 로직에서 받은 true/false 값으로 팔로우/언팔로우합니다.
    
2. **`viewMyFollower/followMeUsers`** 메서드
   * `클라이언트`가 보낸 검색어(`NULL` 과 빈 공백문자 포함)와 유저 코드를 받아 서비스 로직에서 필터링 된 유저 리스트를 가져옵니다.
