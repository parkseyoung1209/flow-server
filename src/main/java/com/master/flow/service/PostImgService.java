package com.master.flow.service;

import com.master.flow.model.dao.PostImgDAO;
import com.master.flow.model.vo.PostImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostImgService {

    @Autowired
    private PostImgDAO postImgDAO;

    // 사진 업로드
    public PostImg addImg(PostImg postImg){
        return postImgDAO.save(postImg);
    }

    // 게시글 삭제
    public void deleteAll(int postCode){
        postImgDAO.deleteById(postCode);
    }

    public List<PostImg> findByPost_PostCode(int postCode) {
        return postImgDAO.findByPost_PostCode(postCode);
    }
}
