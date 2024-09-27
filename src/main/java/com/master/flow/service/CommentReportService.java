package com.master.flow.service;

import com.master.flow.model.dao.CommentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentReportService {

    @Autowired
    private CommentDAO commentDAO;
}
