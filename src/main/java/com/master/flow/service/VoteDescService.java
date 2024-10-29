package com.master.flow.service;

import com.master.flow.model.dao.VoteDescDAO;
import com.master.flow.model.vo.VoteDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteDescService {
    
    @Autowired
    private VoteDescDAO voteDescDao;

    // 투표 내용 조회
    public VoteDesc voteDesc(int voteCode) {
        return voteDescDao.findById(voteCode).get();
    }
}
