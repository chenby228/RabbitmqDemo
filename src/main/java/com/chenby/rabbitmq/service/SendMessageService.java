package com.chenby.rabbitmq.service;

import java.util.List;

public interface SendMessageService {

    List<String> getOnline();

    String pushMessage(String body);

    String checkonline(String body);
}

