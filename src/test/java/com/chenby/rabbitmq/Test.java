package com.chenby.rabbitmq;

import java.util.Base64;

public class Test {

    public static void main(String[] args) {
        String s = Base64.getEncoder().encodeToString("admin:admin".getBytes());
        byte[] decode = Base64.getDecoder().decode(s);
        String s1 = new String(decode);
        System.out.println(s);
        System.out.println(s1);
    }
}
