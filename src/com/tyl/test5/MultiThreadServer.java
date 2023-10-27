package com.tyl.test5;

import java.io.BufferedWriter;
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
 * @date 2023/10/27
 */
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bosskey = ssc.register(boss, 0, null);
        bosskey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8888));
        while (true){
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if(key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                }
            }
        }
    }
    class Worker implements Runnable{

        private Thread thread;
        private Selector worker;
        private String name;
        private volatile  Boolean start=false;

        public Worker(String name) throws IOException {
            if(!start) {
                Thread thread = new Thread(this, name);
                thread.start();
                worker = Selector.open();
                start=true;
            }
        }

        @Override
        public void run() {
           while (true){
               try {
                   worker.select();
                   Iterator<SelectionKey> iter = worker.selectedKeys().iterator();
                   while (iter.hasNext()){
                       SelectionKey key = iter.next();
                       iter.remove();
                       if(key.isReadable()){
                           ByteBuffer buffer=ByteBuffer.allocate(16);
                           SocketChannel sc=(SocketChannel) key.channel();
                           sc.read(buffer);
                           buffer.flip();
                           System.out.println(new String(buffer.array(),0,buffer.remaining()));
                       }

                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }
    }
}
