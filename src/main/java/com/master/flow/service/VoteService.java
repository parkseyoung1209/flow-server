package com.master.flow.service;

import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.VoteDAO;
import com.master.flow.model.vo.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    @Autowired
    private VoteDAO voteDao;

    @Autowired
    private PostDAO postDao;

    @Autowired
    private JPAQueryFactory queryFactory;

    private final QPost qPost = QPost.post;
    private final QVote qVote = QVote.vote;

    // 로그인한 사람 정보 가져오기 - ID
    public int loginId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.isAuthenticated()){
            User user = (User) auth.getPrincipal();
            return user.getUserCode();
        }
        return 0;
    }

    // 찬성/반대 투표
    public void vote (Vote vo){
        voteDao.save(vo);
    }

    // 해당 게시물의 해당유저의 투표 유무 조회
    public Vote checkVote(int userCode, int postCode){
        Vote vote = new Vote();
        try{
            vote = queryFactory
                    .selectFrom(qVote)
                    .where(qVote.post.postCode.eq(postCode))
                    .where(qVote.user.userCode.eq(userCode))
                    .fetch().get(0);
        } catch (Exception e) {
            return null;
        }
        System.err.println(vote);
        return vote;
    }

    // 투표 취소
    public void removeVote (int voteCode){
        voteDao.deleteById(voteCode);
    }

    // 전체 투표 현황
    public int voteCount (int voteCode) {
        return voteDao.count(voteCode);
    }

    // 찬성 투표 현황
    public int voteCountY (int postCode) {
        return voteDao.countY(postCode);
    }

    // 반대 투표 현황
    public int voteCountN (int postCode){
        return voteDao.countN(postCode);
    }

    // 로그인한 사람의 투표체크 여부
    public Vote check(int userCode) {
        return voteDao.check(loginId());
    }



    // 해당 유저의 투표 생성 유무 확인
    public boolean haveVote(int userCode){
        List<Post> post = queryFactory
                .selectFrom(qPost)
                .where(qPost.user.userCode.eq(userCode))
                .where(qPost.postType.eq("vote"))
                .fetch();

        if(post.size() != 0){
            return true;
        }
        return false;
    }
}
