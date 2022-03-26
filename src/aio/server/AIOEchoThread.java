package aio.server;

import address.ServerProperties;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * AIOEcho服务器端线程类
 */
public class AIOEchoThread implements Runnable {
    // 设置一个线程等待的机制
    private CountDownLatch latch;
    // 异步服务器端Socket通道
    private AsynchronousServerSocketChannel serverSocketChannel;
    public AIOEchoThread() throws Exception {
        this.latch = new CountDownLatch(1); // 设置计数器为1,若latch减到0将释放所有线程
        this.serverSocketChannel = AsynchronousServerSocketChannel.open(); // 开启服务器端
        this.serverSocketChannel.bind(new InetSocketAddress(ServerProperties.SERVER_PORT)); // 绑定连接地址
    }
    public AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }
    public CountDownLatch getLatch() {
        return latch;
    }
    @Override
    public void run() {
        this.serverSocketChannel.accept(this,new AcceptHandler()); // 连接回调
        System.out.println("【AIO服务器端已开启,等待客户端连接...】");
        try {
            this.latch.await(); // 进入阻塞状态
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
