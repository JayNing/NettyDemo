package com.ning.netty.v2;

import com.ning.netty.v2.NettyClintHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
 * ClassName: AppClientHello
 * Description: 客户端启动类,根据服务器端的IP和端口，建立连接，连接建立后，实现消息的双向传输----李大爷
 * date: 2021/3/2 12:14
 *
 * @author ningjianjian
 */
public class AppClientHello {
    private String host;

    private int port;

    public static Map<Integer, String> li_contentMap;

    //用于消息交互
    private Map<String, Channel> channelMap = new HashMap<>();

    public Map<String, Channel> getChannelMap() {
        return channelMap;
    }

    public synchronized void setChannel(String name, Channel channel) {
        this.channelMap.put(name, channel);
    }

    static {
        li_contentMap = new HashMap<>();
        li_contentMap.put(0,"刚吃");
        li_contentMap.put(1,"嗨，没事儿溜溜弯儿");
        li_contentMap.put(2,"回头去给老太太请安");
    }

    public AppClientHello(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception
    {

        /**

         * @Description  配置相应的参数，提供连接到远端的方法

         **/

        EventLoopGroup group = new NioEventLoopGroup();//I/O线程池

        try{
            Bootstrap bs = new Bootstrap();//客户端辅助启动类
            bs.group(group)
                    .channel(NioSocketChannel.class)//实例化一个Channel
                    .remoteAddress(new InetSocketAddress(host,port))
                    .handler(new ChannelInitializer<SocketChannel>()//进行通道初始化配置
            {

                @Override

                protected void initChannel(SocketChannel socketChannel) throws Exception{
                    socketChannel.pipeline().addLast(new NettyClintHandler(AppClientHello.this));//添加我们自定义的Handler
                }
            });

            //连接到远程节点；等待连接完成
            ChannelFuture future = bs.connect().sync();

            future.channel().closeFuture().sync();
        } finally{
            group.shutdownGracefully().sync();
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

    public static void main(String[] args) throws Exception
    {
        AppClientHello clientHello = new AppClientHello("127.0.0.1", 18080);
        new Thread() {
            @Override
            public void run() {
                try {
                    clientHello.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Scanner scanner = new Scanner(System.in);
        String msg = "";
        while (!(msg = scanner.nextLine()).equals("exit")) {
            clientHello.writeMsg(msg);
        }

    }

    /**
     * 由于代码中已经添加了详尽的注释，这里只对极个别的进行说明：
     *
     * 1）ChannelInitializer：通道Channel的初始化工作，如加入多个handler，都在这里进行；
     *
     * 2）bs.connect().sync()：这里的sync()表示采用的同步方法，这样连接建立成功后，才继续往下执行；
     *
     * 3）pipeline()：连接建立后，都会自动创建一个管道pipeline，这个管道也被称为责任链，保证顺序执行，同时又可以灵活的配置各类Handler，这是一个很精妙的设计，既减少了线程切换带来的资源开销、避免好多麻烦事，同时性能又得到了极大增强。
     */
}
