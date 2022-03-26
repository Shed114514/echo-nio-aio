package nio.client;

import address.ServerProperties;
import util.InputUtil;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * NIOEcho模型客户端
 */
public class NIOEchoClient {
    /**
     * 开启客户端
     */
    public void start() throws Exception {
        SocketChannel socketChannel = SocketChannel.open(); // 开启Socket客户端通道
        socketChannel.connect(new InetSocketAddress(ServerProperties.SERVER_HOST,ServerProperties.SERVER_PORT));
        ByteBuffer buffer = ByteBuffer.allocate(100); // 通过缓冲区进行交互
        boolean flag = true;
        while (flag) {
            buffer.clear(); // 先清空缓冲区,因为循环会持续进行
            String writeMessage = InputUtil.getString("请输入要发送的消息: ");
            buffer.put(writeMessage.getBytes(StandardCharsets.UTF_8));
            buffer.flip();
            socketChannel.write(buffer); // 将缓冲区中的数据写入客户端Channel
            buffer.clear(); // 清空缓冲区,等待接收回应信息
            int readCount = socketChannel.read(buffer); // 向缓冲区读取数据
            System.out.println(new String(buffer.array(),0,readCount));
            if ("exit".equalsIgnoreCase(writeMessage)) {
                flag = false;
            }
        }
        socketChannel.close();
    }
    public static void main(String[] args) throws Exception {
        // 以主方法函数进行启动客户端
        new NIOEchoClient().start();
    }
}
