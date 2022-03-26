package aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

// 若此类的回调连接成功,那么一定要返回有一个AsynchronousSocketChannel的对象,进行服务器端与客户端通讯
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,AIOEchoThread> {
    @Override
    public void completed(AsynchronousSocketChannel channel, AIOEchoThread aioEchoThread) {
        aioEchoThread.getServerSocketChannel().accept(aioEchoThread,this); //创建一个连接
        ByteBuffer buffer = ByteBuffer.allocate(100); // 分配一个缓冲区
        channel.read(buffer,buffer,new EchoHandler(channel)); // 创建另外一个回调处理
    }
    @Override
    public void failed(Throwable exc, AIOEchoThread attachment) {
        System.err.println("【AcceptHandler】服务器端出现了错误: " + exc.getMessage());
    }
}
