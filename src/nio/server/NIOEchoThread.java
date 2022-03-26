package nio.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 创建一个NIOEcho模型线程
 */
public class NIOEchoThread implements Runnable {
    private boolean flag = true;
    // 客户端通道
    private SocketChannel socketChannel;
    // 注入客户端通道数据
    public NIOEchoThread(SocketChannel socketChannel) throws Exception {
        this.socketChannel = socketChannel;
        System.out.println("[Thread]【客户端连接成功】连接的客户端的地址为：" + this.socketChannel.getRemoteAddress());
    }
    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(100); // 开辟容量为100的缓冲区
        try {
            while (this.flag) {
                buffer.clear(); // 清空缓冲区的数据
                int readCount = this.socketChannel.read(buffer); // 读取buffer缓冲区数据个数
                String readMessage = new String(buffer.array(),0,readCount).trim(); // 字节数据转换为字符串
                System.out.println("[Thread]【服务器端收到的消息】" + readMessage);
                String writeMessage = "[Thread]【Echo】" + readMessage; // 设置回应的消息内容
                if ("exit".equalsIgnoreCase(readMessage)) {
                    writeMessage = "[Thread]【Echo】客户端关闭...";
                    this.flag = false;
                }
                buffer.clear(); // 之前已读取过的数据,先清空缓冲区
                buffer.put(writeMessage.getBytes(StandardCharsets.UTF_8)); // 保存要回应的数据到缓冲区
                buffer.flip(); // 重置缓冲区
                this.socketChannel.write(buffer); // 客服端通道写入缓冲区的数据
            }
            this.socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
