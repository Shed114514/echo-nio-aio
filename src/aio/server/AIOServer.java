package aio.server;

public class AIOServer {
    public static void main(String[] args) throws Exception {
        // 通过创建一个线程对AIOEchoThread类进行启动
        new Thread(new AIOEchoThread()).start();
    }
}
