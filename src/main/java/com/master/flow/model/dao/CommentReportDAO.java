package com.master.flow.model.dao;

import com.master.flow.model.vo.CommentReport;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentReportDAO extends JpaRepository<CommentReport, Integer> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM comment_report WHERE post_code =:postCode",nativeQuery = true)
    public void deleteCommentReportByPostCode(@Param("postCode") int postCode);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM comment_report WHERE comment_code =:commentCode",nativeQuery = true)
    public void deleteCommentReportByCommentCode(@Param("commentCode") int commentCode);
}
