package com.junwangit.nio.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @description: NIO服务端
 * @author: hanfeng
 * @date: 2020-3-6
 **/
@Slf4j
public class NIOServer {
    /**
     * 缓冲区，对应Channel的通信方式
     */
    private ServerSocketChannel server;
    /**
     * 服务默认端口
     */
    int port = 8080;
    /**
     * 多路注册复用器，注册SelectKey_OP各种操作事件
     */
    private Selector selector;
    /**
     * 接收缓冲池
     */
    ByteBuffer recBuffer = ByteBuffer.allocate(1024);
    /**
     * 发送缓冲池
     */
    ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    /**
     * 缓存机制
     */
    Map<SelectionKey, String> sessiomMsg = new HashMap<SelectionKey, String>();
    /**
     * 对客户端编号
     */
    private static int client_no = 19056;

    public NIOServer(int port) throws IOException {
        this.port = port;
        server = ServerSocketChannel.open();
        //底层就是一个ServerSocket
        server.socket().bind(new InetSocketAddress(this.port));
        server.configureBlocking(false);
        /**
         * 再将blocking设为false后，开启selector
         */
        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        log.info("NIO消息服务器初始化完成，可以随时接收客户端链接，监听端口:{}", this.port);

    }

    /**
     * 我们需要用一个线程去监听selector，看上边是否有满足我们需要的事件类型SelectionKey
     *
     * @throws IOException
     */
    public void listener() throws IOException {
        while (true) {
            int evenCount = selector.select();
            if (evenCount == 0) {
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            //遍历并处理监听到selector中的事件
            final Iterator<SelectionKey> iteratorKeys = keys.iterator();
            while (iteratorKeys.hasNext()) {
                process(iteratorKeys.next());
                iteratorKeys.remove();

            }
        }
    }

    /**
     * 这里就是用来处理每一个SelectionKey：包含通道Channel信息 和 selector信息
     *
     * @param key
     */
    private void process(SelectionKey key) {
        SocketChannel client = null;
        try {
            if (key.isValid() && key.isAcceptable()) {
                client = server.accept();
                ++client_no;
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ);
            } else if (key.isValid() && key.isReadable()) {
                //服务器从SocketChannel读取客户端发送过来的信息
                recBuffer.clear();
                client = (SocketChannel) key.channel();
                int len = client.read(recBuffer);
                if (len > 0) {
                    String msg = new String(recBuffer.array(), 0, len);
                    sessiomMsg.put(key, msg);
                    log.info("当前维护的线程ID:{},客户端编号为:{},信息为:{}" ,Thread.currentThread().getId(), client_no, msg);

                    //改变状态，又会被监听器监听到
                    client.register(selector, SelectionKey.OP_WRITE);
                }
            } else if (key.isValid() && key.isWritable()) {
                if (!sessiomMsg.containsKey(key)) {
                    return;
                }
                client = (SocketChannel) key.channel();
                //position=0
                sendBuffer.clear();
                //如position=500
                sendBuffer.put((sessiomMsg.get(key) + "你好，已经处理完请求！").getBytes());
                //limit=500 position=0 0-->limt
                sendBuffer.flip();
                client.write(sendBuffer);
                log.info("当前维护的线程ID:{},对客户端写信息,客户端编号为:{}" ,Thread.currentThread().getId(), client_no);
                //改变状态，又会被监听器监听到
                client.register(selector, SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            //防止客户端非法下线
            key.cancel();
            try {
                client.socket().close();
                client.close();
                log.info("【系统提示】:{}", new SimpleDateFormat().format(new Date()) + sessiomMsg.get(key) + "已下线");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
