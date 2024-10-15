package com.master.flow.model.dao;

import com.master.flow.model.vo.UserReport;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserReportDAO extends JpaRepository<UserReport, Integer> {
//    수정필요
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_report WHERE user_code = :userCode",nativeQuery = true)
    void deleteUserReportByUserCode(@Param("userCode") int userCode);

    @Query(value = "SELECT * FROM user_report WHERE user_code = :userCode",nativeQuery = true)
    void getAllUserReportByUserCode(@Param("userCode") int userCode);

    //    유저 밴
    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET USER_BAN_STATUS = :userBanStatus, USER_BAN_DATE = now(), USER_BAN_COUNT = USER_BAN_COUNT + 1 WHERE USER_CODE =:code",nativeQuery = true)
    void banUser(@Param("code") int code, @Param("userBanStatus") String userBanStatus);
}
