package com.junwangit.socket.server;

import java.io.IOException;

/**
 * @description: 说明描述
 * @author: hanfeng
 * @date: 2020-3-6
 **/
public class StartSocket {
    public static void main(String[] args) {
        try {
            Thread t = new SocketServer(19090);
            t.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
