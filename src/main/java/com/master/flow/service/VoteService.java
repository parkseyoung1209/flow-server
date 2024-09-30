package com.master.flow.service;

import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.VoteDAO;
import com.master.flow.model.vo.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    @Autowired
    private VoteDAO voteDao;
    @Autowired
    private PostDAO postDao;

    // 내가 한 투표 변경 (찬성 -> 반대 or 반대 -> 찬성)
//    public void changVote(Vote vo) {
//        voteDao.save(vo);
//    }
}
