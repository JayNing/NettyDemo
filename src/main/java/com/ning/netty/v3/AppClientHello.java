package com.ning.netty.v3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

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

    public AppClientHello(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception
    {

        /**

         * @Description  配置相应的参数，提供连接到远端的方法

         **/

        EventLoopGroup group = new NioEventLoopGroup(1);//I/O线程池

        try{
            Bootstrap bs = new Bootstrap();//客户端辅助启动类
            bs.group(group)
                    .channel(NioSocketChannel.class)//实例化一个Channel
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536))
                    .remoteAddress(new InetSocketAddress(host,port))
                    .handler(new ChannelInitializer<SocketChannel>()//进行通道初始化配置
            {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception{
                    String delimiter = "_$";

                    ChannelPipeline pipeline = socketChannel.pipeline();

//                    pipeline.addLast(new LineBasedFrameDecoder(1024));
//                    pipeline.addLast(new StringDecoder());
//                    pipeline.addLast(new StringEncoder());

                    pipeline.addLast(new DelimiterBasedFrameDecoder(1024,
                            Unpooled.wrappedBuffer(delimiter.getBytes())));
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new DelimiterBasedFrameEncoder(delimiter));

                    pipeline.addLast(new NettyClintHandler());//添加我们自定义的Handler
                }
            });

            //连接到远程节点；等待连接完成
            ChannelFuture future = bs.connect().sync();

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
