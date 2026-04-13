package com.project.test.mapper;

import com.project.test.DTO.LoginRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LevelUserMapper {
    
    LoginRequest findById(@Param("id") Integer id);
    
    LoginRequest findByUsername(@Param("username") String username);
    
    int updateLevelById(@Param("id") Integer id, @Param("level") Integer level);
    
    int insert(LoginRequest user);
}
