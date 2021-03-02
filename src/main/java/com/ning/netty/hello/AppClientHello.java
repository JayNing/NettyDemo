package com.ning.netty.hello;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * ClassName: AppClientHello
 * Description: 客户端启动类,根据服务器端的IP和端口，建立连接，连接建立后，实现消息的双向传输
 * date: 2021/3/2 12:14
 *
 * @author ningjianjian
 */
public class AppClientHello {
    private String host;

    private int port;

    public static Map<Integer, String> contentMap;

    static {
        contentMap = new HashMap<Integer,String>();
        contentMap.put(0,"吃了没，您呐？");
        contentMap.put(1,"您这，嘛去？");
        contentMap.put(2,"有空家里坐坐啊");
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
                    socketChannel.pipeline().addLast(new NettyClintHandler());//添加我们自定义的Handler
                }
            });

            //连接到远程节点；等待连接完成
            ChannelFuture future = bs.connect().sync();

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            //发送消息到服务器端，编码格式是utf-8
            int count = 100000;
            long start = System.currentTimeMillis();
            while (true){
                count--;
                String s = contentMap.get(new Random().nextInt(3));
                future.channel().writeAndFlush(Unpooled.copiedBuffer(s, CharsetUtil.UTF_8));
//                future.channel().writeAndFlush(Unpooled.copiedBuffer(in.readLine(), CharsetUtil.UTF_8));
                if (count < 0){
                    break;
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("耗时：" + (end - start) + "ms");

            //阻塞操作，closeFuture()开启了一个channel的监听器（这期间channel在进行各项工作），直到链路断开
            future.channel().closeFuture().sync();

        } finally{
            group.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) throws Exception
    {

        new AppClientHello("127.0.0.1",18080).run();

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
