package com.chenby.rabbitmq.listener;

import com.alibaba.fastjson.JSONObject;
import com.chenby.rabbitmq.until.HttpUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.chenby.rabbitmq.until.MQUtils.*;

@Component
public class MessageReceive {



    private static final Logger logger = Logger.getLogger("MessageReceive",null);

    @Autowired
    private HttpUtils httpUtil;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = PRODUCER_QUEUE_NAME, declare = "true"),
            exchange = @Exchange(name = EXCHANGE_NAME, declare = "true"),
            key = PRODUCER_ROUTINGKEY
    ))
    @RabbitHandler
    public void process(String msg){
        //处理客户端发送来的消息
        logger.info( "来自APP的消息：" + msg);
    }
}