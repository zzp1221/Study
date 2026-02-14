package com.example.book_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookDemoApplication.class, args);
        Demo demo = new Demo();
        demo.setId(1);
        System.out.println(demo);
    }

}
