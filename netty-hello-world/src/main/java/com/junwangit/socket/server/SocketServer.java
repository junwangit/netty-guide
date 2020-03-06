package com.junwangit.socket.server;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @description: 说明描述
 * @author: hanfeng
 * @date: 2020-3-6
 **/
@Slf4j
public class SocketServer extends Thread {

    private ServerSocket serverSocket;

    public SocketServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(100000);
    }

    @Override
    public void run() {
        while (true) {
            try {
                log.debug("等待远程连接，端口号为：{}", serverSocket.getLocalPort());
                Socket server = serverSocket.accept();
                log.debug("远程主机地址：{}", server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());
                log.debug(in.readUTF());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF("感谢光临我的服务：" + server.getLocalSocketAddress());
                server.close();
            } catch (SocketTimeoutException s) {
                log.debug("Socket 连接超时");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
