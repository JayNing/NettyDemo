package com.ning.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ClassName: NingServer
 * Description:
 * date: 2021/3/2 18:25
 *
 * @author ningjianjian
 */
public class NingServer {
    public static void main(String[] args) {

        new Thread(() -> {
            try {
                ServerSocket socket = new ServerSocket(1234);
                while (true) {
                    Socket accept = socket.accept();
                    // 每一个新的连接都创建一个线程，负责读取数据
                    new Thread(() -> {
                        InputStream inputStream = null;
                        try {
                            inputStream = accept.getInputStream();
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = inputStream.read(buffer)) > 0){
                                System.out.println("接收到客户端消息：" + new String(buffer,0,len));
                                accept.getOutputStream().write((len + ",服务端收到消息了").getBytes());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
