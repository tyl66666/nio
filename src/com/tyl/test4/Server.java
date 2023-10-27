package com.tyl.test4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author tyl
 * @date 2023/10/26
 */
public class Server {
    // 解决文件大小超出ByteBufferlength问题
    private static void split(ByteBuffer source){
        source.flip();
        for(int i=0;i<source.limit();i++){
            if(source.get(i)=='\n'){
                int length=i+1-source.position();
                ByteBuffer target=ByteBuffer.allocate(length);
                for(int j=0;j<length;i++){
                    target.put(source.get());
                }
                target.flip();
                String msg=new String(target.array(),0,target.remaining());
                System.out.println(msg);
            }
        }
        source.compact();
    }
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
            //4 处理实践，selectedKeys 内部包含了所有发生的事件  但是当selectedKeys中事件处理完毕之后 并不会自己删除事件 得手动删除
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                // 删除事件
                iter.remove();
                if(key.isAcceptable()){
                    ServerSocketChannel channel=(ServerSocketChannel) key.channel();
                    // accept 建立与客户端连接，SocketChannel 用来与客户端之间通信 阻塞方法
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    // SocketChannel与selector建立连接
                    // 将ByteBuffer当成一个附件 放在key上  保证了 一个通道对应这唯一的一个ByteBuffer
                    ByteBuffer buffer=ByteBuffer.allocate(16);
                    SelectionKey sckey = sc.register(selector, 0, buffer);
                    // 注册读事件
                    sckey.interestOps(SelectionKey.OP_READ);
                }else if(key.isReadable()){
                    // 这里之所以要抛出异常： 因为客户端发送完就关闭了  关闭客户端之后 read方法执行就会报错
                   try {
                       SocketChannel sc=(SocketChannel)  key.channel();
                       // 获取附件的内容
                       ByteBuffer buffer =(ByteBuffer) key.attachment();
                       int read = sc.read(buffer);
                       if(read==-1){
                           key.cancel();
                       }else {
                           split(buffer);
                           if(buffer.position()==buffer.limit()){
                               ByteBuffer newbuffer=ByteBuffer.allocate(buffer.capacity()<<2);
                               buffer.flip();
                               newbuffer.put(buffer);
                               key.attach(newbuffer);
                           }
                       }
                   }catch (IOException e){
                       e.printStackTrace();
                       key.cancel();
                   }
                }
            }
        }
    }
}
