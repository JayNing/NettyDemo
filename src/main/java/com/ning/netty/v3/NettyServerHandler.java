package com.ning.netty.v3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

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

      @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  throws Exception
    {

//        System.out.println(msg);
        //处理收到的数据，并反馈消息到到客户端
        String clientMsg = (String) msg;
        if ("吃了没，您呐？".equals(clientMsg)){
            ctx.channel().writeAndFlush(Unpooled.copiedBuffer("刚吃。" + DELIMITER,CharsetUtil.UTF_8));
        } else if ("您这，嘛去？".equals(clientMsg)){
            ctx.channel().writeAndFlush(Unpooled.copiedBuffer("嗨，没事儿溜溜弯儿。" + DELIMITER,CharsetUtil.UTF_8));
        } else if ("有空家里坐坐啊。".equals(clientMsg)){
            ctx.channel().writeAndFlush(Unpooled.copiedBuffer("回头去给老太太请安！" + DELIMITER,CharsetUtil.UTF_8));
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
