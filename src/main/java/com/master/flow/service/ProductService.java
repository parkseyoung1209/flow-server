package com.master.flow.service;

import com.master.flow.model.dao.ProductDAO;
import com.master.flow.model.vo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
