package com.example.book_demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/book")
public class BookController {
    @RequestMapping("/getlist")
    public List<BookInfo> getBookList(){
        List<BookInfo> book = new ArrayList<BookInfo>();
        for (BookInfo bookInfo : book){
            if (bookInfo.getStatus()==1){
                bookInfo.setStatusCN("可借阅");
            }else {
                bookInfo.setStatusCN("不可借阅");
            }
        }
        return book;
    }

    public List<BookInfo> mockDate(){
        List<BookInfo> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookId(i);
            bookInfo.setBookName("图书"+i);
            bookInfo.setAuthor("作者"+i);
            bookInfo.setNum(new Random().nextInt(100));
            bookInfo.setPrice(new BigDecimal(new Random().nextInt(100)));
            bookInfo.setPublish("出版社"+i);
            bookInfo.setStatus(i%5==0?2:1);
            list.add(bookInfo);
        }
        return list;
    }

}
