package com.ning.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Date;

/**
 * ClassName: NingClient
 * Description:
 * date: 2021/3/2 18:25
 *
 * @author ningjianjian
 */
public class NingClient {
    public static void main(String[] args) {
        // TODO 创建多个线程，模拟多个客户端连接服务端
        new Thread(() -> {
            long start = System.currentTimeMillis();
            int count = 100000;
            while (true) {
                count--;
                if (count < 0){
                    break;
                }
                Socket socket = null;
                try {
                    socket = new Socket("127.0.0.1", 1234);
                    socket.getOutputStream().write(("你好啊," + new Date()).getBytes("UTF-8"));
//                    Thread.sleep(1000L);
                    Socket finalSocket = socket;
                    new Thread(() -> {
                        InputStream inputStream = null;
                        try {
                            inputStream = finalSocket.getInputStream();
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = inputStream.read(buffer)) > 0){
                                System.out.println("接收到服务端消息：" + new String(buffer,0,len));
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("耗时：" + (end - start) + "ms");
        }).start();
    }
}
