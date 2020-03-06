package com.junwangit.nio.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @description: NIO客户端
 * @author: hanfeng
 * @date: 2020-3-6
 **/
@Slf4j
public class NIOClient {
    private SocketChannel client;

    InetSocketAddress serverAddress = new InetSocketAddress("localhost", 19090);

    private Selector selector;

    /**
     * 接收缓冲池
     */
    ByteBuffer recBuffer = ByteBuffer.allocate(1024);
    /**
     * 发送缓冲池
     */
    ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

    public NIOClient() throws IOException {
        //构造client实例
        client = SocketChannel.open();

        client.configureBlocking(false);
        client.connect(serverAddress);

        //构造selector实例
        selector = Selector.open();

        //注册连接事件
        client.register(selector, SelectionKey.OP_CONNECT);
        //Netty Reactor线程池组 Tomcat bootstrap
    }

    public void session() throws IOException {

        if (client.isConnectionPending()) {
            client.finishConnect();

            client.register(selector, SelectionKey.OP_WRITE);

           log.info("已经连接到服务器，可以在控制台登记了");

        }

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String msg = sc.nextLine();
            if ("".equals(msg)) {
                continue;
            }
            if ("exit".equals(msg)) {
                System.exit(0);
            }


            process(msg);

        }
    }

    private void process(String name) {
        boolean waitHelp = true;
        Iterator<SelectionKey> iteratorKeys = null;
        Set<SelectionKey> keys = null;
        while (waitHelp) {
            try {

                int readys = selector.select();

                //如果没有客人，继续轮询
                if (readys == 0) {
                    continue;
                }

                keys = selector.selectedKeys();
                iteratorKeys = keys.iterator();

                //一个个迭代keys
                while (iteratorKeys.hasNext()) {
                    SelectionKey key = iteratorKeys.next();

                    if (key.isValid() && key.isWritable()) {

                        //可写就是客户端要对服务器发送信息
                        sendBuffer.clear();
                        sendBuffer.put(name.getBytes());
                        sendBuffer.flip();
                        client.write(sendBuffer);
                        client.register(selector, SelectionKey.OP_READ);
                    } else if (key.isValid() && key.isReadable()) {
                        //服务器发送信息回来，给客户端读
                        recBuffer.clear();
                        int len = client.read(recBuffer);
                        if (len > 0) {
                            recBuffer.flip();
                            log.info("服务器返回的消息是: 当前维护的线程ID:{},对客户端写信息:{}" ,Thread.currentThread().getId(), new String(recBuffer.array(), 0, len));

                            //改变状态，又会被监听器监听到
                            client.register(selector, SelectionKey.OP_WRITE);

                            waitHelp = false;
                        }
                    }
                    //检查完之后，打发客户走
                    iteratorKeys.remove();
                }

            } catch (IOException e) {
                //防止客户端非法下线
                ((SelectionKey) keys).cancel();
                try {
                    client.socket().close();
                    client.close();
                    return;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }

    }
}
