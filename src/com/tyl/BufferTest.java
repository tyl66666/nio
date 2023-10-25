package com.tyl;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author tyl
 * @date 2023/10/22
 */
public class BufferTest {
    public static void main(String[] args) {
        // 1 分配一个缓冲区，容量设置为10
        ByteBuffer buffer=ByteBuffer.allocate(10);
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());
        System.out.println("---------------------------------------");

        //2 put往缓冲区中添加数据
        String name ="tyl";
        buffer.put(name.getBytes(StandardCharsets.UTF_8));
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());
        System.out.println("---------------------------------------");

        // 3 Buffer flip() 为将缓冲区的界限设置为当前位置，并将当前位置设置为0 （俗称可读模式）
        buffer.flip();
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());
        System.out.println("---------------------------------------");

        // 4 get数据的读取
        char ch = (char) buffer.get();
        char ch1 = (char) buffer.get();
//        byte[] bt=new byte[2];
//        ByteBuffer byteBuffer = buffer.get(bt);
//        char ch = (char) byteBuffer.get();
        System.out.println(ch);
        System.out.println(ch1);
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        System.out.println("---------------------------------------");
         // clear清除缓冲区中的数据 (注意的是 只是将position的位置归0 而不会清除数据)
         buffer.clear();

        System.out.println("---------------------------------------");
        ByteBuffer buffer2=ByteBuffer.allocate(10);
        buffer2.put(name.getBytes(StandardCharsets.UTF_8));

        // 转换成读模式
        buffer2.flip();

        // get(byte[] )方法  这样就可以输出两个字节
        byte[] bt=new byte[2];
        buffer2.get(bt);
        System.out.println(new String(bt));

        // mark()标记方法 这个方法就是可以标记现在的位置 可以通过reset() 方法会到标记得位置  mark()与reset() 这两个方法一般用在一起
        buffer2.mark();

        // hasRemaining() 方法查看在position与limit 之间是否有可读数据
        buffer2.hasRemaining();

        // remaining 查看在position与limit 之间是否有可读数据的个数


    }
}
