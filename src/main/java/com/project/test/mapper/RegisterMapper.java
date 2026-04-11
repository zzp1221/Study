package com.project.test.mapper;

import com.project.test.DTO.RegisterUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegisterMapper {


    int insert(RegisterUser registerUser);
}
