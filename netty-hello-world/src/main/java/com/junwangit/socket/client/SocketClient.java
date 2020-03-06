package com.junwangit.socket.client;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @description: 说明描述
 * @author: hanfeng
 * @date: 2020-3-6
 **/
@Slf4j
public class SocketClient {

    /**
     * 客户端推送消息
     *
     * @param host
     * @param port
     */
    public void push(String host, int port) {
        try {
            Socket client = new Socket(host, port);
            log.debug("远程主机地址：{}", client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("Hello from " + client.getLocalSocketAddress());
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            log.debug("服务器响应：{}", in.readUTF());

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
