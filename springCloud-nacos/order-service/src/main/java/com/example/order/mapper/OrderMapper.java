package com.example.order.mapper;

import com.example.order.model.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface OrderMapper {
    @Select("select * from order_detail where id=#{orderId}")
    public OrderInfo selectOrderById(Integer orderId);
}
