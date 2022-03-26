package aio.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class ClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    // 设置一个线程等待的机制
    private CountDownLatch latch;
    // 异步Socket通道
    private AsynchronousSocketChannel socketChannel;
    public ClientReadHandler(AsynchronousSocketChannel socketChannel, CountDownLatch latch) {
        this.latch = latch;
        this.socketChannel = socketChannel;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip(); // 重设缓冲区,准备接收数据
        String msg = new String(buffer.array(),0,buffer.remaining()).trim(); // 从缓冲区读取数据
        System.out.println(msg);
    }
    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.err.println("【ClientReadHandler】数据读取错误,请重新连接服务器...");
        try {
            this.socketChannel.close();
            this.latch.countDown(); // countDown到0时,主线程会停止
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
