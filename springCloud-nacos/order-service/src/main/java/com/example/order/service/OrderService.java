package com.example.order.service;

import com.example.order.mapper.OrderMapper;
import com.example.order.model.OrderInfo;
import com.example.order.model.ProductInfo;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RestTemplate restTemplate;


    public OrderInfo selectOrderById(Integer orderId){
        OrderInfo orderInfo = orderMapper.selectOrderById(orderId);
        String url = "http://product-service/product/"+orderInfo.getProductId();
        ProductInfo productInfo = restTemplate.getForObject(url, ProductInfo.class);
        orderInfo.setProductInfo(productInfo);
        return orderInfo;
    }
}
