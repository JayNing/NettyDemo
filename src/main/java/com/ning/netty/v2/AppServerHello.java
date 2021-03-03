package com.ning.netty.v2;

import com.ning.netty.v2.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * ClassName: AppServerHello
 * Description: 服务器端启动类----张大爷
 * date: 2021/3/2 12:28
 *
 * @author ningjianjian
 */
public class AppServerHello {

    private int port;

    public AppServerHello(int port){
        this.port = port;
    }

    public static Map<Integer, String> zhang_contentMap;

    //用于消息交互
    private Map<String, Channel> channelMap = new HashMap<>();

    public Map<String, Channel> getChannelMap() {
        return channelMap;
    }

    public synchronized void setChannel(String name, Channel channel) {
        this.channelMap.put(name, channel);
    }

    static {
        zhang_contentMap = new HashMap<>();
        zhang_contentMap.put(0,"吃了没，您呐？");
        zhang_contentMap.put(1,"您这，嘛去？");
        zhang_contentMap.put(2,"有空家里坐坐啊");
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();//Netty的Reactor线程池，初始化了一个NioEventLoop数组，用来处理I/O操作,如接受新的连接和读/写数据
        try{
            ServerBootstrap b = new ServerBootstrap();//用于启动NIO服务
            b.group(group)
                    .channel(NioServerSocketChannel.class) //通过工厂方法设计模式实例化一个channel
                    .localAddress(new InetSocketAddress(port))//设置监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                //ChannelInitializer是一个特殊的处理类，他的目的是帮助使用者配置一个新的Channel,用于把许多自定义的处理类增加到pipline上来
                @Override
                public void initChannel(SocketChannel ch) throws Exception {//ChannelInitializer 是一个特殊的处理类，他的目的是帮助使用者配置一个新的 Channel。
                    ch.pipeline().addLast(new NettyServerHandler(AppServerHello.this));//配置childHandler来通知一个关于消息处理的InfoServerHandler实例
                }
            });

            //绑定服务器，该实例将提供有关IO操作的结果或状态的信息
            ChannelFuture channelFuture = b.bind().sync();

            System.out.println("在"+ channelFuture.channel().localAddress()+"上开启监听");

            //阻塞操作，closeFuture()开启了一个channel的监听器（这期间channel在进行各项工作），直到链路断开
            channelFuture.channel().closeFuture().sync();

        } finally{
            group.shutdownGracefully().sync();//关闭EventLoopGroup并释放所有资源，包括所有创建的线程
        }
    }

    public boolean writeMsg(String msg) {
        boolean errorFlag = false;
        Map<String, Channel> channelMap = getChannelMap();
        if (channelMap.size() == 0) {
            return true;
        }
        Set<String> keySet = channelMap.keySet();
        for (String key : keySet) {
            try {
                Channel channel = channelMap.get(key);
                if (!channel.isActive()) {
                    errorFlag = true;
                    continue;
                }
                ByteBuf byteBuf = Unpooled.copiedBuffer(msg, Charset.forName("utf-8"));
                channel.writeAndFlush(byteBuf);
            } catch (Exception e) {
                errorFlag = true;
            }
        }
        return errorFlag;
    }
    public static void main(String[] args)  throws Exception {
        AppServerHello nettyServer = new AppServerHello(18080);
        new Thread() {
            @Override
            public void run() {
                try {
                    nettyServer.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Scanner scanner = new Scanner(System.in);
        String msg = "";
        while (!(msg = scanner.nextLine()).equals("exit")) {
            nettyServer.writeMsg(msg);
//            System.out.println(nettyServer.writeMsg(zhang_contentMap.get(Integer.valueOf(msg))));
        }

    }

    /**
     * 代码说明：
     *
     * 1）EventLoopGroup：实际项目中，这里创建两个EventLoopGroup的实例，一个负责接收客户端的连接，另一个负责处理消息I/O，这里为了简单展示流程，让一个实例把这两方面的活都干了；
     *
     * 2）NioServerSocketChannel：通过工厂通过工厂方法设计模式实例化一个channel，这个在大家还没有能够熟练使用Netty进行项目开发的情况下，不用去深究。
     */
}
