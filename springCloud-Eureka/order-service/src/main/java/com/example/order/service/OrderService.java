package com.example.order.service;

import com.example.order.mapper.OrderMapper;
import com.example.order.model.OrderInfo;
import com.example.order.model.ProductInfo;
import jakarta.annotation.PostConstruct;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    private AtomicInteger counter = new AtomicInteger(1);

    private List<ServiceInstance> serviceInstances;

    @PostConstruct
    public void init(){
        //从Eureka中获取
        serviceInstances = discoveryClient.getInstances("product-service");
    }

//    public OrderInfo selectOrderById(Integer orderId){
//        OrderInfo orderInfo = orderMapper.selectOrderById(orderId);
////        String url = "http://127.0.0.1:8.81/product/"+orderInfo.getProductId();
//        //从Eureka中获取
//        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("product-service");
//        String uri = serviceInstances.get(0).getUri().toString();
//        String url = uri+"/product/"+orderInfo.getProductId();
//        ProductInfo productInfo = restTemplate.getForObject(url, ProductInfo.class);
//        orderInfo.setProductInfo(productInfo);
//        return orderInfo;
//    }


//    public OrderInfo selectOrderById(Integer orderId){
//        OrderInfo orderInfo = orderMapper.selectOrderById(orderId);
////        String url = "http://127.0.0.1:8.81/product/"+orderInfo.getProductId();
//        //计算实例
//        int index = counter.getAndIncrement()%serviceInstances.size();
//        //获取实例
//        String uri = serviceInstances.get(index).getUri().toString();
//        //拼接url
//        String url = uri+"/product/"+orderInfo.getProductId();
//        ProductInfo productInfo = restTemplate.getForObject(url, ProductInfo.class);
//        orderInfo.setProductInfo(productInfo);
//        return orderInfo;
//    }
//}

    public OrderInfo selectOrderById(Integer orderId){
        OrderInfo orderInfo = orderMapper.selectOrderById(orderId);
        String url = "http://product-service/product/"+orderInfo.getProductId();

        ProductInfo productInfo = restTemplate.getForObject(url, ProductInfo.class);
        orderInfo.setProductInfo(productInfo);
        return orderInfo;
    }
}








