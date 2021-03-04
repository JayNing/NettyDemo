package com.ning.netty.v3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName: NettyClintHandler
 * Description: 通用handler，处理I/O事件
 * date: 2021/3/2 12:10
 *
 * @author ningjianjian
 */
@ChannelHandler.Sharable //@ChannelHandler.Sharable这个注解是为了线程安全，如果你不在乎是否线程安全，不加也可以；
public class NettyClintHandler extends ChannelInboundHandlerAdapter {

//    private static String DELIMITER = "\n";
    private static String DELIMITER = "_$";

    private static AtomicInteger count = new AtomicInteger(0);
//    private static AtomicInteger count1 = new AtomicInteger(1);
//    private static AtomicInteger count2 = new AtomicInteger(1);
//    private static AtomicInteger count3 = new AtomicInteger(1);

    private static volatile int count1 = 1;
    private static volatile int count2 = 1;
    private static volatile int count3 = 1;

    private long start;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object  msg) throws Exception {
        /**

         * @Description  处理接收到的消息

         **/
//        System.out.println(msg);
        String fromServer = (String) msg;
        if ("刚吃。".equals(fromServer)){
            ctx.write(Unpooled.copiedBuffer("您这，嘛去？"+DELIMITER,CharsetUtil.UTF_8));
            if (count1 % 50000 == 0){
                ctx.flush();
            }
            count1++;
        } else if ("嗨，没事儿溜溜弯儿。".equals(fromServer)){
            ctx.write(Unpooled.copiedBuffer("有空家里坐坐啊。"+DELIMITER,CharsetUtil.UTF_8));
            if (count2 % 50000 == 0){
                ctx.flush();
            }
            count2++;
        }else if ("回头去给老太太请安！".equals(fromServer)){
            int incrementAndGet = count.incrementAndGet();
            if (count3 % 50000 == 0){
                ctx.flush();
                System.out.println("第" + incrementAndGet + "次");
            }
            count3++;
            if (incrementAndGet == 100000){
                long end = System.currentTimeMillis();
                System.out.println("耗时：" + (end - start) + "ms");
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.start = System.currentTimeMillis();
        for (int i = 1; i <= 100000; i++){
            ctx.write(Unpooled.copiedBuffer("吃了没，您呐？"+DELIMITER,CharsetUtil.UTF_8));
            if (i % 50000 == 0){
                ctx.flush();
            }
        }
    }

    @Override

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**

         * @Description  处理I/O事件的异常

         **/

        cause.printStackTrace();

        ctx.close();

    }

    /**
     * 代码说明：
     *
     * 1）@ChannelHandler.Sharable：这个注解是为了线程安全，如果你不在乎是否线程安全，不加也可以；
     *
     * 2）SimpleChannelInboundHandler：这里的类型可以是ByteBuf，也可以是String，还可以是对象，根据实际情况来；
     *
     * 3）channelRead0：消息读取方法，注意名称中有个0；
     *
     * 4）ChannelHandlerContext：通道上下文，代指Channel；
     *
     * 5）ByteBuf：字节序列，通过ByteBuf操作基础的字节数组和缓冲区，因为JDK原生操作字节麻烦、效率低，所以Netty对字节的操作进行了封装，实现了指数级的性能提升，同时使用更加便利；
     *
     * 6）CharsetUtil.UTF_8：这个是JDK原生的方法，用于指定字节数组转换为字符串时的编码格式。
     */
}
