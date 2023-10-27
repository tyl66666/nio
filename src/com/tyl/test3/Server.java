package com.tyl.test3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * @author tyl
 * @date 2023/10/26
 */
public class Server {
    public static void main(String[] args) throws IOException {
        // 1 创建selector ,管理多个channel
        Selector selector= Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2 建立 selector和channel的联系
        // SelectionKey 就是将来事件发生后通过它可以知道事件和那个channel的时事件关联
        SelectionKey ssckey = ssc.register(selector, 0, null);
        //key 只关注accept事件
        ssckey.interestOps(SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8888));
        while(true){
            //3 select方法没有事件发生，线程阻塞 有事件，线程才会恢复运行
            // 当未处理事件时 是不阻塞的,但当事件处理完毕 或 事件取消时 就会阻塞在这 (简单理解就是要处理新的事件)
            selector.select();
            //4 处理实践，selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()){
//                SelectionKey key = iter.next();
//                ServerSocketChannel channel=(ServerSocketChannel) key.channel();
//                channel.accept();

                ssckey.cancel();
            }
        }
    }
}
