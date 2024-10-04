package com.master.flow.model.dao;

import com.master.flow.model.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDAO extends JpaRepository<User, Integer> {

    @Query(value="SELECT * FROM USER WHERE USER_CODE = :code", nativeQuery = true)
    User findByCode(@Param("code") int code);

    // 가입시 회원 중복 체크
    @Query(value = "SELECT * FROM USER WHERE USER_EMAIL = :userEmail AND USER_PLATFORM = :userPlatform", nativeQuery = true)
    Optional<User> duplicateCheck(@Param("userEmail") String userEmail, @Param("userPlatform") String userPlatform);
}
