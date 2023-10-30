package com.tyl.test5;

import com.sun.corba.se.spi.orbutil.threadpool.Work;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tyl
 * @date 2023/10/27
 * 注意： 当代码执行顺序有前后关系是 最好是在同一个线程 不要将代码放在不同线程 这个无法做到准确的前后关系
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
        //创建固定数量的worker 并初始化
        Worker[] workers =new Worker[Runtime.getRuntime().availableProcessors()];
        Worker worker=null;
        for(int i=0;i<workers.length;i++){
             workers[i] = new Worker("woker-"+i);
        }
        AtomicInteger aount=new AtomicInteger();
        while (true){
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if(key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    workers[aount.incrementAndGet()%workers.length].register(sc);
                }
            }
        }
    }
    static class Worker implements Runnable{

        private Thread thread;
        private Selector selector;
        private String name;
        /**
         * 使用ConcurrentLinkedQueue存储需要执行的任务
         * 亮点！！！！
         */
        private ConcurrentLinkedQueue<Runnable> queue=new ConcurrentLinkedQueue<>();
        private volatile  Boolean start=false;

        public Worker(String name)  {
             this.name=name;
        }

        //初始化线程 和selector
        public void register( SocketChannel sc) throws IOException {
            if(!start) {
                thread = new Thread(this, name);
                thread.start();
                selector = Selector.open();
                start=true;
            }
            queue.add(()->{
                try {
                    sc.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();
        }

        @Override
        public void run() {
           while (true){
               try {
                   selector.select();
                   Runnable poll = queue.poll();
                   if(poll!=null){
                       poll.run();
                   }
                   Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
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
