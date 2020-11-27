package com.chenby.rabbitmq.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chenby.rabbitmq.service.SendMessageService;
import com.chenby.rabbitmq.until.MQUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.chenby.rabbitmq.until.MQUtils.*;
import static com.chenby.rabbitmq.until.MQUtils.EXCHANGE_NAME;

@Service
public class SendMessageServiceImpl implements SendMessageService {

    private final Logger logger = Logger.getLogger("SendMessageService", null);


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public List<String> getOnline() {
        List<String> list = new ArrayList<>();
        String apiMessage = MQUtils.getApiMessage("");
        JSONArray consumerList = JSON.parseArray(apiMessage);
        for (Object object : consumerList){
            String consumers = JSON.parseObject(object.toString()).getJSONObject("queue").getString("name");
            if (!PRODUCER_QUEUE_NAME.equals(consumers)){
                String consumer = consumers.replace(CONSUMER_QUEUE_NAME, "");
                list.add(consumer);
            }
        }
        return list;
    }

    @Override
    public String pushMessage(String body) {
        JSONObject jsonBody = JSON.parseObject(body);
        String queueName = CONSUMER_QUEUE_NAME + jsonBody.getString("employeeID");
        String routingKey = CONSUMER_ROUTINGKEY + jsonBody.getString("employeeID");
        if (!MQUtils.isExistsQueue(queueName)) {
            try {
                String apiMessage = MQUtils.getQueueApi();
                JSONArray array = JSON.parseArray(apiMessage);
                for (Object object : array){
                    MQUtils.storageQueue(JSON.parseObject(object.toString()).getString("name"));
                }
            } catch (IOException e) {
                logger.info(e.getMessage());
                e.printStackTrace();
            }
        }

        if (MQUtils.isExistsQueue(queueName)){
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, body);
            logger.info("推送消息成功");
//            return "推送消息成功";
            return "200";
        }else {
            logger.info("推送消息异常");
//            return "推送消息异常";
            return "201";
        }
    }

    @Override
    public String checkonline(String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        String replace = jsonObject.getString("employeeIDList").replace("[", "").replace("]", "").replace(" ", "");
        String[] split = replace.split(",");
        List<String> online = getOnline();
        List<String> list = new ArrayList<>();
        for (String user : split){
            if (online.contains(user)){
                list.add(user);
            }
        }
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("onlineList", list);
        return jsonObject1.toString();
    }
}


