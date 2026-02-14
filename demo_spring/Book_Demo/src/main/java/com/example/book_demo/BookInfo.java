package com.example.book_demo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookInfo {
    private String bookName;
    private String author;
    private String publish;
    private Integer bookId;
    private BigDecimal price;
    private Integer status;
    private  Integer num;
    private String statusCN;
}
