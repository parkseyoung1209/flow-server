package com.master.flow.model.dao;

import com.master.flow.model.vo.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDAO extends JpaRepository<User, Integer> {

    @Query(value="SELECT * FROM USER WHERE USER_CODE = :code", nativeQuery = true)
    User findByCode(@Param("code") int code);

    // 가입시 회원 중복 체크
    @Query(value = "SELECT * FROM USER WHERE USER_EMAIL = :userEmail AND USER_PLATFORM = :userPlatform", nativeQuery = true)
    Optional<User> duplicateCheck(@Param("userEmail") String userEmail, @Param("userPlatform") String userPlatform);

//    유저 밴
    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET USER_BAN_STATUS = :userBanStatus, USER_BAN_DATE = now(), USER_BAN_COUNT = USER_BAN_COUNT + 1 WHERE USER_CODE =:code",nativeQuery = true)
    void banUser(@Param("code") int code, @Param("userBanStatus") String userBanStatus);
}
