package com.chenby.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.chenby.rabbitmq.until.MQUtils.*;

@Configuration
public class DirectRabbitConfig {

    public Queue queue(){
        return new Queue(PRODUCER_QUEUE_NAME, true);
    }

    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE_NAME, true ,false);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(exchange()).with(PRODUCER_ROUTINGKEY);
    }
}