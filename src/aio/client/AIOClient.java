package aio.client;

import util.InputUtil;

public class AIOClient {
    public static void main(String[] args) throws Exception {
        AIOClientThread clientThread = new AIOClientThread();
        // 通过创建一个线程对AIOClientThread类进行启动
        new Thread(clientThread).start();
        while (clientThread.sendMessage(InputUtil.getString("[AIO]请输入要发送的消息: ").trim())) {
            // 让客户端线程一直调用消息发送操作方法,直到返回false跳出循环
        }
    }
}
