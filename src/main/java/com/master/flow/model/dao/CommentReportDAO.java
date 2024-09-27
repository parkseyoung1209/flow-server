package com.master.flow.model.dao;

import com.master.flow.model.vo.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportDAO extends JpaRepository<CommentReport, Integer> {
}
