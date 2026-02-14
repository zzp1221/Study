package com.example.demo;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

public class Jackson {
    @Test
    public void test() {
        ObjectMapper mapper = new ObjectMapper();
        //创建Java对象
        UserInfo user = new UserInfo("1",2,"3");
        user.setAge(18);
        user.setUserName("zzz");
        user.setGender("a");
        String json = mapper.writeValueAsString(user);
        System.out.println(json);
    }

    @Test
    public void test2() {
        ObjectMapper mapper = new ObjectMapper();

        String str = "{\"age\":18,\"userName\":\"zzz\",\"gender\":\"a\"}";

        UserInfo userInfo = mapper.readValue(str, UserInfo.class);
        System.out.println(userInfo);
    }
}
