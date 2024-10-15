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

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post_report WHERE post_code =:postCode",nativeQuery = true)
    void deletePostReportByPostCode(@Param("postCode") int postCode);

    @Modifying
    @Transactional
    @Query(value="INSERT INTO post_report(post_report_desc,post_code) VALUES(:postReportDesc,:postCode)",nativeQuery = true)
    void insertPostReport(@Param("postReport") PostReport postReport);
}
