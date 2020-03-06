package com.junwangit.nio;

import com.junwangit.nio.client.NIOClient;
import org.junit.Test;

import java.io.IOException;

/**
 * @description: 说明描述
 * @author: hanfeng
 * @date: 2020-3-6
 **/
public class NIOTest {
    @Test
    public void push() {
        try {
            new NIOClient().session();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
