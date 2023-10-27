package com.tyl.test1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tyl
 * @date 2023/10/26
 */
public class Server1 {
    public static void main(String[] args) throws IOException {
        // 使用nio 来处理阻塞模式
        // 0 ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //1 创建服务器
        ServerSocketChannel ssc=ServerSocketChannel.open();
//        ssc.configureBlocking(false);
        //2 绑定监听端口
        ssc.bind(new InetSocketAddress((8888)));

        //连接
        List<SocketChannel> channels=new ArrayList<>(10);
        while(true){
            //4 accept 建立与客户端连接，SocketChannel 用来与客户端之间通信 阻塞方法
            SocketChannel sc = ssc.accept();
//            sc.configureBlocking(false);
            System.out.println("a");
            channels.add(sc);
            for(SocketChannel channel: channels){
                //5 接收客户端发送的数据 阻塞方法
                channel.read(buffer);
                buffer.flip();
                String rs=new String(buffer.array(),0,buffer.remaining());
                System.out.println(rs);
            }
        }
    }
}
