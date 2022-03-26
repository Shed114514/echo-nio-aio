package aio.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class ClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
    // 设置一个线程等待的机制
    private CountDownLatch latch;
    // 异步Socket通道
    private AsynchronousSocketChannel socketChannel;
    public ClientWriteHandler(AsynchronousSocketChannel socketChannel,CountDownLatch latch) throws Exception {
        this.latch = latch;
        this.socketChannel = socketChannel;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if (buffer.hasRemaining()) { // 若缓冲区存在数据,向Socket通道写入缓冲区的数据
            this.socketChannel.write(buffer,buffer,this);
        } else { // 若缓冲区不存在数据进行写入,则需要考虑读取问题
            ByteBuffer readBuffer = ByteBuffer.allocate(100);
            this.socketChannel.read(readBuffer,readBuffer,new ClientReadHandler(this.socketChannel,this.latch));
        }
    }
    @Override
    public void failed(Throwable exc, ByteBuffer buffer) {
        System.err.println("【ClientWriteHandler】数据写入错误,请重新连接服务器...");
        try {
            this.socketChannel.close();
            this.latch.countDown(); // countDown到0时,主线程会停止
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
