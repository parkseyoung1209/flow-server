package com.master.flow.model.dao;

import com.master.flow.model.vo.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImgDAO extends JpaRepository<PostImg, Integer> {
    List<PostImg> findByPost_PostCode(int postCode);
}
