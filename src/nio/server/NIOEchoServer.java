package nio.server;

import address.ServerProperties;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NIOEcho模型服务器端
 */
public class NIOEchoServer {
    /**
     * 开启服务器端
     */
    public void start() throws Exception {
        // 1. 考虑到线程性能的分配处理问题,需要创建一个定长的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        // 2. 若要实现服务器端的开发,那么一定需要有一个服务器的Channel管理
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open(); // 开启服务器端处理通道
        serverSocketChannel.configureBlocking(false); // 设置为非阻塞模式
        serverSocketChannel.bind(new InetSocketAddress(ServerProperties.SERVER_PORT)); // 在当前主机中的端口号9999绑定服务
        // 3. 开启一个选择器,所有的Channel均注册到此选择器当中,利用此选择器的循环来判断是否有新的连接
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // 注册通道,处理连接
        // 4. 所有的用户的连接都要注册到当前的多路复用器上,那么需要利用循环的模式来进行用户状态的判断
        int keySelect = 0; // 保存用户的判断状态
        while ((keySelect = selector.select()) > 0) { // 持续进行等待,直到用用户连接上
            Set<SelectionKey> selectionKeySet = selector.selectedKeys(); // 获取当前所有的连接信息
            Iterator<SelectionKey> selectionKeyIterator = selectionKeySet.iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next(); // 获取当前的处理状态
                if (selectionKey.isAcceptable()) { // 若当前为一个连接请求方式
                    SocketChannel socketChannel = serverSocketChannel.accept(); // 创建客户端通道
                    if (socketChannel != null) {
                        threadPool.submit(new NIOEchoThread(socketChannel)); // 启动一个处理线程
                    }
                }
                selectionKeyIterator.remove(); // 若已经进行了处理,则不需要后续的轮询操作
            }
        }
        threadPool.shutdown();
        serverSocketChannel.close();
    }
    public static void main(String[] args) throws Exception {
        // 以主方法函数进行启动服务器端
        new NIOEchoServer().start();
    }
}
