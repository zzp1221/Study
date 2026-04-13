package com.project.test.controller;

import com.project.test.common.result.Result;
import com.project.test.entity.InterfaceLevel;
import com.project.test.service.LevelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/level")
@AllArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @GetMapping("/interfaces")
    public Result getAllInterfaceLevels() {
        List<InterfaceLevel> levels = levelService.getAllInterfaceLevels();
        return Result.success(levels);
    }

    @GetMapping("/interface/{id}")
    public Result getInterfaceLevel(@PathVariable Long id) {
        InterfaceLevel interfaceLevel = levelService.getInterfaceLevelById(id);
        if (interfaceLevel == null) {
            return Result.error("接口等级配置不存在");
        }
        return Result.success(interfaceLevel);
    }

    @PostMapping("/interface")
    public Result createInterfaceLevel(@RequestBody InterfaceLevel interfaceLevel) {
        try {
            levelService.createInterfaceLevel(interfaceLevel);
            log.info("创建接口等级配置成功：{} {}", interfaceLevel.getInterfacePath(), interfaceLevel.getInterfaceMethod());
            return Result.success("创建成功", null);
        } catch (Exception e) {
            log.error("创建接口等级配置失败：{}", e.getMessage());
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    @PutMapping("/interface")
    public Result updateInterfaceLevel(@RequestBody InterfaceLevel interfaceLevel) {
        try {
            levelService.updateInterfaceLevel(interfaceLevel);
            log.info("更新接口等级配置成功：{} {}", interfaceLevel.getInterfacePath(), interfaceLevel.getInterfaceMethod());
            return Result.success("更新成功", null);
        } catch (Exception e) {
            log.error("更新接口等级配置失败：{}", e.getMessage());
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/interface/{id}")
    public Result deleteInterfaceLevel(@PathVariable Long id) {
        try {
            levelService.deleteInterfaceLevel(id);
            log.info("删除接口等级配置成功，ID: {}", id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除接口等级配置失败：{}", e.getMessage());
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
