package com.master.flow.service;

import com.master.flow.model.dao.CommentReportDAO;
import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.PostImgDAO;
import com.master.flow.model.dao.ProductDAO;
import com.master.flow.model.dto.PostDTO;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import com.master.flow.model.vo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostDAO postDao;
    private PostImgDAO postImgDao;
    private ProductDAO productDao;

    // 멀티 파트 파일 받으려면 아예 DTO 추가
    // DTO 하나에 받고자 하는 것들 다!
    // List<Multipartfile>
    // files[0]
    // files[1]
    //게시물 업로드
    public void change(PostDTO dto){
        // postDao.save();
       // List<MultipartFile> img = dto.getFiles();
        List<Product> pd = dto.getProducts();

        /*
        for(MultipartFile file : img){
            System.out.println(file);
        }*/
        for(Product p : pd){
            System.out.println(p);
        }

    }
}
