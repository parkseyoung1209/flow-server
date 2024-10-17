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

    // 메인화면 게시글 이미지
    public List<PostImg> findByPost_PostCode(int postCode) {

        return postImgDAO.findByPost_PostCode(postCode);
    }

    // 게시물 수정 시 postImgCode로 사진 삭제
    public void updateImages(int postImgCode){
        postImgDAO.deleteById(postImgCode);
    }

    // primary key로 불러오기
    public PostImg fetchImg(int postImgCode){

        return postImgDAO.findById(postImgCode).get();
    }


}
