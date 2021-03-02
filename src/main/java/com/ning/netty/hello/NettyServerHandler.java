package com.ning.netty.hello;

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

    public static Map<String, String> contentMap;

    static {
        contentMap = new HashMap<String,String>();
        contentMap.put("吃了没，您呐？","刚吃");
        contentMap.put("您这，嘛去？","嗨，没事儿溜溜弯儿");
        contentMap.put("有空家里坐坐啊","回头去给老太太请安");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  throws Exception
    {

        //处理收到的数据，并反馈消息到到客户端
        ByteBuf in = (ByteBuf) msg;
        String clientMsg = in.toString(CharsetUtil.UTF_8);
        System.out.println("客户端: "+ clientMsg);
        //写入并发送信息到远端（客户端）
        String returnMsg = contentMap.get(clientMsg);
        if (returnMsg != null){
            ctx.writeAndFlush(Unpooled.copiedBuffer(returnMsg, CharsetUtil.UTF_8));
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
