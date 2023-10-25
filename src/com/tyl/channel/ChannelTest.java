package com.tyl.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author tyl
 * @date 2023/10/22
 */
public class ChannelTest {
    public static void main(String[] args) {
        try {
            // 1. 字节输出流通向目标文件
            FileOutputStream fos=new FileOutputStream("data01.txt");
            // 2. 得到字节输出流对应的通道Channel1
            FileChannel channel = fos.getChannel();
            // 3. 分配缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("hello,world".getBytes(StandardCharsets.UTF_8));
            // 4.把缓冲区切换成写出模式
            buffer.flip();
            channel.write(buffer);
            channel.close();
            System.out.println("写数据到文件中");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read(){
        try {
            // 1.定义一个文件字节输入流与源文件接通
            FileInputStream is=new FileInputStream("data01.txt");
            // 2.需要得到文件字节输入流的文件通道
            FileChannel channel=is.getChannel();
            // 3. 定义一个缓冲区
            ByteBuffer buffer =ByteBuffer.allocate(1024);
            // 4. 读取数据缓冲区
            channel.read(buffer);
            buffer.flip();
            //5. 读取出缓冲区中的数据并输出即可
            String rs=new String(buffer.array(),0,buffer.remaining());
            System.out.println(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void copy() throws Exception {
        // 源文件
        File srcFile =new File("a");
        File destFile =new File("b");

        // 得到一个字节输出流
        FileInputStream fis=new FileInputStream(srcFile);
        // 得到一个字节输出流
        FileOutputStream fos=new FileOutputStream(destFile);
        // 得到的是文件通道
        FileChannel isChannel=fis.getChannel();
        FileChannel ioChannel=fos.getChannel();

        // 分配缓冲区
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        while (true){
            //必须先清空缓冲然后在写入数据到缓冲区
            buffer.clear();
            //开始读取一个数据
            int flag=isChannel.read(buffer);
            if(flag==-1){
                break;
            }

            // 已经读取到数据，把缓冲区的模式u切换成可读模式
            buffer.flip();
            // 把数据写出
            ioChannel.write(buffer);
        }
        isChannel.close();
        ioChannel.close();
    }

    public void duohuancun() throws Exception {
        // 1 字节输入管道
        FileInputStream is=new FileInputStream("a");
        FileChannel isChannel = is.getChannel();
        //2 字节输出管道
        FileOutputStream fos=new FileOutputStream("b");
        FileChannel osChannel = fos.getChannel();
        //3 定义多个缓冲区做数据分散
        ByteBuffer buffer1=ByteBuffer.allocate(4);
        ByteBuffer buffer2=ByteBuffer.allocate(1024);
        ByteBuffer[] buffers={buffer1,buffer2};
        //4 从通道中读取数据分散到各个缓冲区
        isChannel.read(buffers);
        //5 从每个缓冲区中查询是否有数据读到了
        for(ByteBuffer buffer: buffers){
            buffer.flip();
            System.out.println(new String(buffer.array(),0,buffer.remaining()));
        }
        // 6.聚集写到通道
        osChannel.write(buffers);
        isChannel.close();
        osChannel.close();
    }
}
