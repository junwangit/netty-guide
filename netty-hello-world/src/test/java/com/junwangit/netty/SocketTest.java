package com.junwangit.netty;

import com.junwangit.netty.client.SocketClient;
import org.junit.Test;

/**
 * @description: 说明描述
 * @author: hanfeng
 * @date: 2020-3-6
 **/
public class SocketTest {

    @Test
    public void pushTest() {
        SocketClient client = new SocketClient();
        client.push("127.0.0.1", 19090);
    }
}
