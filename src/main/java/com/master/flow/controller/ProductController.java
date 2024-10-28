package com.master.flow.controller;

import com.master.flow.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class ProductController {

    @Autowired
    private ProductService productService;

    @DeleteMapping("/product")
    public ResponseEntity deleteProduct(@RequestBody List<Integer> products){

//        System.out.println(products);

        for (Integer p : products) {
            if(p!=null) productService.deleteOne(p);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
