package com.master.flow.model.dao;

import com.master.flow.model.vo.PostReport;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostReportDAO extends JpaRepository<PostReport, Integer> {
    @Query(value = "SELECT * FROM post_report WHERE post_code = :postCode",nativeQuery = true)
    List<PostReport> findPostReportByPostCode(@Param("postCode") int postCode);

//    130번 삭제시 130번으로 동일한 신고들 삭제안됨 
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post_report WHERE post_code =:postCode",nativeQuery = true)
    void deletePostReportByPostCode(@Param("postCode") int postCode);
}
