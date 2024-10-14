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
}
