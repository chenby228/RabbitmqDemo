package com.chenby.rabbitmq.until;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpUtils {

    public String sendGetRequest(String url){
        return HttpRequest.get(url).execute().body();
    }

    public String sendPostRequest(String url, String body){
        return HttpRequest.post(url)
                .body(body)
                .contentType(String.valueOf(ContentType.JSON))
                .execute()
                .body();
    }
}
