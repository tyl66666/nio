package com.tyl.test2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author tyl
 * @date 2023/10/25
 */
public class Client1 {
    public static void main(String[] args) throws IOException {
        SocketChannel sc=SocketChannel.open();
        // 连接
        sc.connect(new InetSocketAddress("localhost",8888));

        //写数据
        ByteBuffer buffer = Charset.defaultCharset().encode("hello");
        sc.write(buffer);
    }
}
