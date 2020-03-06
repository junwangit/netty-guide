package com.junwangit.nio.server;

import java.io.IOException;

/**
 * @description: 启动NIO服务
 * @author: hanfeng
 * @date: 2020-3-6
 **/
public class StartNIOServer {
    public static void main(String[] args) {
        try {
            new NIOServer(19090).listener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
