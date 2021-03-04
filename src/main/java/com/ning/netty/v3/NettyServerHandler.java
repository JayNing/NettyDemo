package com.ning.netty.v3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName: NettyServerHandler
 * Description:
 * date: 2021/3/2 12:27
 *
 * @author ningjianjian
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

//    private static String DELIMITER = "\n";
    private static String DELIMITER = "_$";

//    private static AtomicInteger count1 = new AtomicInteger(0);
//    private static AtomicInteger count2 = new AtomicInteger(0);
//    private static AtomicInteger count3 = new AtomicInteger(0);

    private static volatile int count1 = 1;
    private static volatile int count2 = 1;
    private static volatile int count3 = 1;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  throws Exception
    {

//        System.out.println(msg);
        //处理收到的数据，并反馈消息到到客户端
        String clientMsg = (String) msg;
        if ("吃了没，您呐？".equals(clientMsg)){
            ctx.write(Unpooled.copiedBuffer("刚吃。" + DELIMITER,CharsetUtil.UTF_8));
            if (count1 % NettyContant.NUM == 0){
                ctx.flush();
            }
            count1++;
        } else if ("您这，嘛去？".equals(clientMsg)){
            ctx.write(Unpooled.copiedBuffer("嗨，没事儿溜溜弯儿。" + DELIMITER,CharsetUtil.UTF_8));
            if (count2 % NettyContant.NUM == 0){
                ctx.flush();
            }
            count2++;
        } else if ("有空家里坐坐啊。".equals(clientMsg)){
            ctx.write(Unpooled.copiedBuffer("回头去给老太太请安！" + DELIMITER,CharsetUtil.UTF_8));
            if (count3 % NettyContant.NUM == 0){
                ctx.flush();
            }
            count3++;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {

        //出现异常的时候执行的动作（打印并关闭通道）

        cause.printStackTrace();

        ctx.close();

    }
}
