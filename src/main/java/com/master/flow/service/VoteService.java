package com.master.flow.service;

import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.VoteDAO;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    @Autowired
    private VoteDAO votedao;
    @Autowired
    private PostDAO postdao;

    // 투표 게시판 전체 조회
    public List<Post> postVoteViewAll(Post vo) {
        return postdao.findPostTypesByVote(vo.getPostType());
    }

    // 내가 한 투표 변경 (찬성 -> 반대 or 반대 -> 찬성)
    public void changVote(Vote vo) {
        votedao.save(vo);
    }
}
