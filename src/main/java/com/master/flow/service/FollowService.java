package com.master.flow.service;

import com.master.flow.model.dao.FollowDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
    @Autowired
    private FollowDAO followDAO;
}
