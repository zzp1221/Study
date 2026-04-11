package com.project.test.mapper;

import com.project.test.DTO.LoginRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AuthorityMapper {

    @Select("SELECT id, username, authority, password FROM user WHERE username = #{username} AND deleteFlag = 0")
    LoginRequest findByUsernameWithAuthority(@Param("username") String username);
    
    @Select("SELECT id, username, authority, password FROM user WHERE id = #{id} AND deleteFlag = 0")
    LoginRequest findByIdWithAuthority(@Param("id") Integer id);
    
    @Update("UPDATE user SET authority = #{authority} WHERE username = #{username}")
    int updateAuthorityByUsername(@Param("username") String username, @Param("authority") String authority);
    
    @Update("UPDATE user SET password = #{password} WHERE username = #{username}")
    int updatePasswordByUsername(@Param("username") String username, @Param("password") String password);
}
