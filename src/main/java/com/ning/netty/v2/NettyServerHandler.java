package com.ning.netty.v2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
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

    private AppServerHello nettyServer;

    public NettyServerHandler(AppServerHello nettyServer) {
        this.nettyServer = nettyServer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  throws Exception
    {

        //处理收到的数据，解析客户端发来的消息
        ByteBuf in = (ByteBuf) msg;
        String clientMsg = in.toString(CharsetUtil.UTF_8);
        System.out.println("李大爷说: "+ clientMsg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String clientName = channel.remoteAddress().toString();
        System.out.println("RemoteAddress:"+clientName+"active!");
        nettyServer.setChannel(clientName, channel);
        super.channelActive(ctx);
        ByteBuf byteBuf = Unpooled.copiedBuffer("你好，李大爷！", Charset.forName("utf-8"));
        channel.writeAndFlush(byteBuf);
//        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {

        //出现异常的时候执行的动作（打印并关闭通道）

        cause.printStackTrace();

        ctx.close();

    }
}
