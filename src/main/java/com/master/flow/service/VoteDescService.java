package com.master.flow.service;

import com.master.flow.model.dao.VoteDescDAO;
import com.master.flow.model.vo.QVoteDesc;
import com.master.flow.model.vo.VoteDesc;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteDescService {
    
    @Autowired
    private VoteDescDAO voteDescDao;

    @Autowired
    private JPAQueryFactory queryFactory;

    private final QVoteDesc qVoteDesc = QVoteDesc.voteDesc;

    public void save(VoteDesc vo){
        voteDescDao.save(vo);
    }

    // 투표 내용 조회
    public VoteDesc voteDesc(int postCode) {
        return queryFactory.selectFrom(qVoteDesc)
                .where(qVoteDesc.post.postCode.eq(postCode))
                .fetch().get(0);
    }
}
