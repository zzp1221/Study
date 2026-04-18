package com.project.test.service;

import com.project.test.DTO.LoginRequest;
import com.project.test.entity.InterfaceLevel;
import com.project.test.mapper.InterfaceLevelMapper;
import com.project.test.mapper.LevelUserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class LevelService {

    private final LevelUserMapper levelUserMapper;
    private final InterfaceLevelMapper interfaceLevelMapper;

    public LoginRequest getUserById(Integer id) {
        return levelUserMapper.findById(id);
    }

    public LoginRequest getUserByUsername(String username) {
        return levelUserMapper.findByUsername(username);
    }

    @Transactional
    public int updateUserLevel(Integer userId, Integer newLevel) {
        return levelUserMapper.updateLevelById(userId, newLevel);
    }

    public List<InterfaceLevel> getAllInterfaceLevels() {
        return interfaceLevelMapper.findAll();
    }

    public InterfaceLevel getInterfaceLevelById(Long id) {
        return interfaceLevelMapper.findById(id);
    }

    public InterfaceLevel getInterfaceLevelByPath(String path, String method) {
        return interfaceLevelMapper.findByPathAndMethod(path, method);
    }

    @Transactional
    public int createInterfaceLevel(InterfaceLevel interfaceLevel) {
        interfaceLevel.setStatus(1);
        return interfaceLevelMapper.insert(interfaceLevel);
    }

    @Transactional
    public int updateInterfaceLevel(InterfaceLevel interfaceLevel) {
        return interfaceLevelMapper.update(interfaceLevel);
    }

    @Transactional
    public int deleteInterfaceLevel(Long id) {
        return interfaceLevelMapper.deleteById(id);
    }
}
