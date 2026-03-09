package com.example.order.controller;

import com.example.order.model.OrderInfo;
import com.example.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order")
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping("/{orderId}")
    public OrderInfo getOrderById(@PathVariable("orderId") Integer orderId){
        return orderService.selectOrderById(orderId);
    }
}
