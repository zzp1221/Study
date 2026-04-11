package com.project.test.mapper;

import com.project.test.DTO.LoginRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password FROM user WHERE username = #{username}  AND deleteFlag = 0")
    LoginRequest findByUsername(@Param("username") String username);
}
