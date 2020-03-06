package com.junwangit.nio.client;

import java.io.IOException;

/**
 * @description: 说明描述
 * @author: hanfeng
 * @date: 2020-3-6
 **/
public class StartNIOClient {
    public static void main(String[] args) {
        try {
            new NIOClient().session();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
