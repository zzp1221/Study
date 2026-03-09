package com.example.product.service;

import com.example.product.mapper.ProductMapper;
import com.example.product.model.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    public ProductInfo selectProductById(Integer id) {
        return productMapper.selectProductById(id);
    }
}
