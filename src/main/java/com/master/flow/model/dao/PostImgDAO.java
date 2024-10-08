package com.master.flow.model.dao;

import com.master.flow.model.vo.PostImg;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImgDAO extends JpaRepository<PostImg, Integer> {
    List<PostImg> findByPost_PostCode(int postCode);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM post_img WHERE post_code = :postCode",nativeQuery = true)
    public void deletePostImgByPostCode(@Param("postCode") int postCode);
}
