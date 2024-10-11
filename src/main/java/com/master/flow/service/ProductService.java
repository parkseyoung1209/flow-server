package com.master.flow.service;

import com.master.flow.model.dao.ProductDAO;
import com.master.flow.model.vo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDAO productDao;

    // 제품 추가
    public Product addProduct(Product product){
        return productDao.save(product);
    }

    // 게시글 전체 삭제
    public void deleteAll(int postCode){
        productDao.deleteById(postCode);
    }

    // postCode로 제품 가져오기
    public List<Product> certainProduct(int postCode){
        return productDao.findByPost_PostCode(postCode);
    }
}
