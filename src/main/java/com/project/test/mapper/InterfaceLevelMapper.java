package com.project.test.mapper;

import com.project.test.entity.InterfaceLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InterfaceLevelMapper {
    
    InterfaceLevel findByPathAndMethod(
        @Param("path") String path, 
        @Param("method") String method
    );
    
    List<InterfaceLevel> findAll();
    
    InterfaceLevel findById(@Param("id") Long id);
    
    int insert(InterfaceLevel interfaceLevel);
    
    int update(InterfaceLevel interfaceLevel);
    
    int deleteById(@Param("id") Long id);
}
