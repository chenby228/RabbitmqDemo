package com.chenby.rabbitmq.until;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


public class MQUtils {
    private static final Logger logger = LoggerFactory.getLogger(MQUtils.class);

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.apiport}")
    private String port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtualHost}")
    private String virtualHost;

    private static List<String> queueList = new ArrayList<>();
    private static List<String> routingKeyList = new ArrayList<>();

    public static final String  EXCHANGE_NAME = "iAlarmExchange";
    public static final String CONSUMER_QUEUE_NAME = "ialarm_CONSUMER_QUEUE_"; //server --> app
    public static final String PRODUCER_QUEUE_NAME = "ialarm_PRODUCER_QUEUE"; //app --> server
    public static final String PRODUCER_ROUTINGKEY = "ialarm_PRODUCER_ROUTINGKEY";
    public static final String CONSUMER_ROUTINGKEY = "ialarm_CONSUMER_ROUTINGKEY_";

    public static void storageQueue(String queueName){
        if (!isExistsQueue(queueName)){
            queueList.add(queueName);
        }
    }

    public static List<String> getQueueList(){
        return queueList;
    }

    public static boolean isExistsQueue(String queueName){
        return queueList.contains(queueName);
    }


    public static void storageRoutingKey(String routingKey){
        routingKeyList.add(routingKey);
    }

    public static List<String> getRoutingKeyList(){
        return routingKeyList;
    }

    public static boolean isExistsRoutingKey(String routingKey){
        return queueList.contains(routingKey);
    }

    /**
     * 队列任务总数
     *
     * @param queueName
     * @return
     */
    public int getMessageCount(String queueName) throws IOException {
        String apiMessage = getApiMessage(queueName);
        if (Objects.equals(apiMessage, "")) {
            logger.error("请求RabbitMQ API时出错！！");
            return 0;
        }
        JSONObject jsonObject = JSON.parseObject(apiMessage);
        return Integer.parseInt(jsonObject.get("messages").toString());
    }

    /**
     * 队列ready任务数
     *
     * @param queueName
     * @return
     */
    public int getMessageReadyCount(String queueName) throws IOException {
        String apiMessage = getApiMessage(queueName);
        if (Objects.equals(apiMessage, "")) {
            logger.error("请求RabbitMQ API时出错！！");
            return 0;
        }
        JSONObject jsonObject = JSON.parseObject(apiMessage);
        return Integer.parseInt(jsonObject.get("messages_ready").toString());
    }

    /**
     * 队列unack数MQ
     *
     * @param queueName
     * @return
     */
    public int getMessagesUnacknowledgedCount(String queueName) throws IOException {
        String apiMessage = getApiMessage(queueName);
        if (Objects.equals(apiMessage, "")) {
            logger.error("请求RabbitMQ API时出错！！");
            return 0;
        }
        JSONObject jsonObject = JSON.parseObject(apiMessage);
        return Integer.parseInt(jsonObject.get("messages_unacknowledged").toString());
    }

    /**
     * 获取队列消息总数、ready消息数、unack消息数
     *
     * @param queueName queueName
     * @return Map<String,Integer>
     */
    public Map<String, Integer> getMQCountMap(String queueName) throws IOException {
        String apiMessage = getApiMessage(queueName);
        JSONObject jsonObject = JSON.parseObject(apiMessage);
        Map<String, Integer> map = new HashMap<>();
        map.put("messages", Integer.parseInt(jsonObject.get("messages").toString()));
        map.put("messages_ready", Integer.parseInt(jsonObject.get("messages_ready").toString()));
        map.put("messages_unacknowledged", Integer.parseInt(jsonObject.get("messages_unacknowledged").toString()));
        return map;
    }

    public static String getApiMessage(String queueName) {
        //发送一个GET请求
        HttpURLConnection httpConn = null;
        BufferedReader in = null;

        String urlString = "http://" + "10.134.82.183" + ":" + 15672 +"/api/consumers";
        try {
            URL url = new URL(urlString);
            httpConn = (HttpURLConnection) url.openConnection();
            //设置用户名密码
            String auth = "admin" + ":" + "admin";
            String encoding = Base64.getEncoder().encodeToString(auth.getBytes());
            httpConn.setDoOutput(true);
            httpConn.setRequestProperty("Authorization", "Basic " + encoding);
            // 建立实际的连接
            httpConn.connect();
            //读取响应
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder content = new StringBuilder();
                String tempStr = "";
                in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                while ((tempStr = in.readLine()) != null) {
                    content.append(tempStr);
                }
                in.close();
                httpConn.disconnect();
                return content.toString();
            } else {
                httpConn.disconnect();
                return "";
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }


    public static String getQueueApi() throws IOException {
        //发送一个GET请求
        HttpURLConnection httpConn = null;
        BufferedReader in = null;

        String urlString = "http://" + "10.134.82.183" + ":" + 15672 +"/api/queues";
        URL url = new URL(urlString);
        httpConn = (HttpURLConnection) url.openConnection();
        //设置用户名密码
        String auth = "admin" + ":" + "admin";
        String encoding = Base64.getEncoder().encodeToString(auth.getBytes());
        httpConn.setDoOutput(true);
        httpConn.setRequestProperty("Authorization", "Basic " + encoding);
        // 建立实际的连接
        httpConn.connect();
        //读取响应
        if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            StringBuilder content = new StringBuilder();
            String tempStr = "";
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            while ((tempStr = in.readLine()) != null) {
                content.append(tempStr);
            }
            in.close();
            httpConn.disconnect();
            return content.toString();
        } else {
            httpConn.disconnect();
            return "";
        }
    }
}


