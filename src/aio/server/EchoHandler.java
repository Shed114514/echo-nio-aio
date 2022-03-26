package aio.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * 定义Echo回调处理的类,读取完成的数据返回的类型为长度
 */
public class EchoHandler implements CompletionHandler<Integer, ByteBuffer> {
    // 异步Socket通道
    private AsynchronousSocketChannel channel;
    // 设置一个退出的标记,若exit = true则结束所有的操作
    private boolean exit = false;
    // 保存客户端的通道,进行消息的接收与发送
    public EchoHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }
    // 进行回应数据的写入处理
    public void echoWrite(String result) {
        ByteBuffer buffer = ByteBuffer.allocate(100); // 进行回应数据的写入
        buffer.put(result.getBytes(StandardCharsets.UTF_8)); // 分配写入的缓存
        buffer.flip(); // 重置缓冲区
        this.channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (buffer.hasRemaining()) { // 如果存在有数据,那么可以进行写入
                    EchoHandler.this.channel.write(buffer,buffer,this); // 进行写入处理回调
                } else { // 若现在不存在任何数据
                    if (EchoHandler.this.exit == false) { // 代表当前还会继续执行
                        ByteBuffer readBuffer = ByteBuffer.allocate(100);
                        EchoHandler.this.channel.read(readBuffer,readBuffer,new EchoHandler(EchoHandler.this.channel));
                    }
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                EchoHandler.this.closeClientChannel();
            }
        });
    }
    // 关闭客户端通道
    private void closeClientChannel() {
        try {
            System.err.println("【EchoHandler】响应操作失败,关闭客户端连接...");
            this.channel.close(); // 若操作失败,则进行通道的关闭
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip(); // AIO的处理是将数据先进行接收,接收完成后开启线程
        // 此时客户端所发送来的数据内容实际上已经被全部接收完毕了
        String readMessage = new String(buffer.array(),0,buffer.remaining()).trim(); // 读取发送来的数据
        System.out.println("【服务器端接收到消息】" + readMessage);
        String resultMessage = "【Echo】"  + readMessage;
        if ("exit".equalsIgnoreCase(readMessage)) {
            resultMessage = "【Exit】关闭客户端...";
            this.exit = true; // 设置结束的标记
        }
        this.echoWrite(resultMessage); // 写入处理
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        this.closeClientChannel();
    }
}
