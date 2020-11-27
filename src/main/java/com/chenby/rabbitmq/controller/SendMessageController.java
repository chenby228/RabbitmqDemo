package com.chenby.rabbitmq.controller;

import com.chenby.rabbitmq.service.SendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@RestController
public class SendMessageController {

    private final Logger logger = Logger.getLogger("SendMessageController", null);

    @Autowired
    private SendMessageService service;
    @GetMapping("/tcpserver/getOnline")
    public List<String> getOnline() throws IOException {
        logger.info("在线查询接口");
        return service.getOnline();
    }
    @PostMapping("/tcpserver/push")
    public String pushMessage(@RequestBody String body){
        logger.info("推送消息接口");
        logger.info("请求内容：" + body );
        String s = service.pushMessage(body);
        logger.info("返回内容" + s);
        return s;
    }

    @PostMapping("/tcpserver/checkonline")
    public String tcpCheckOnline(@RequestBody String body){
        logger.info("获取在线用户接口");
        logger.info("请求内容：" + body );
        String checkonline = service.checkonline(body);
        logger.info("返回内容" + checkonline);
        return checkonline;
    }
}