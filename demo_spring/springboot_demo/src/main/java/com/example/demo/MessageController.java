package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/message")
public class MessageController {
    List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
    @PostConstruct
    @PostMapping(value = "/publish",produces = "application/json")
    public String publishMessage(@RequestBody MessageInfo messageInfo) {
        if (!StringUtils.hasLength(messageInfo.getFrom())||
        !StringUtils.hasLength(messageInfo.getTo())||
        !StringUtils.hasLength(messageInfo.getMsg())) {
            return "{\"ok\":false}";
        }
        messageInfos.add(messageInfo);
        return "{\"ok\":true}";
    }
    @GetMapping("/get")
    public List<MessageInfo> getMessageInfos() {
        return messageInfos;
    }
}
