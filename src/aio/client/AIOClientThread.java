package aio.client;

import address.ServerProperties;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AIOClientThread implements Runnable {
    // 设置一个线程等待的机制
    private CountDownLatch latch;
    // 异步Socket通道
    private AsynchronousSocketChannel socketChannel;
    public AIOClientThread() throws Exception {
        this.latch = new CountDownLatch(1); // 设置计数器默认值为1,当count减到0则释放所有线程
        this.socketChannel = AsynchronousSocketChannel.open(); // 开启异步Socket通道
        // Socket通道连接到指定的主机和端口号
        this.socketChannel.connect(new InetSocketAddress(ServerProperties.SERVER_HOST,ServerProperties.SERVER_PORT));
    }
    // 定义一个消息发送的处理方法
    public boolean sendMessage(String msg) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(100); // 开辟缓冲区
        buffer.put(msg.getBytes(StandardCharsets.UTF_8)); // 保存数据到缓存区之中
        buffer.flip(); // 重置缓冲区
        this.socketChannel.write(buffer,buffer,new ClientWriteHandler(this.socketChannel,this.latch));
        if ("exit".equalsIgnoreCase(msg)) {
            TimeUnit.SECONDS.sleep(1);
            this.latch.countDown(); // 计数器减1至0,释放线程
            return false;
        }
        return true;
    }
    @Override
    public void run() {
        try {
            this.latch.await(); // 进入阻塞状态
            this.socketChannel.close(); // 关闭客户端连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
