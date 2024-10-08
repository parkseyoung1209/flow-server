package com.master.flow.model.dao;

import com.master.flow.model.vo.PostReport;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostReportDAO extends JpaRepository<PostReport, Integer> {
}
