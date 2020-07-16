import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    private int flag = 1;

//    buffer size
//    private int blockSize = 4096;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(4096); // send buffer
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(4096);  // receive buffer

//    declare selector
    private final Selector selector;

    public NIOServer(int port) throws IOException {
//        server channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false); // not block

//        init serverSocket
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));

//        open selector(return selector)
        selector = Selector.open();
//        register the event
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server start -> " + port);
    }

//    listen
    public void listen() throws IOException {
//        轮询（持续监听）
        while(true) {
//            遍历key，看哪个key有反应
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator(); // 遍历器
//            开始遍历存在的响应
            while(iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
//                业务逻辑  <-- work place -->
                handlerKey(selectionKey);
            }
        }
    }

    public void handlerKey(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel server = null;
        SocketChannel client = null;

//        data received
        String receiveText;
        String sendText;
        int count = 0;
        if(selectionKey.isAcceptable()) {
//            如果发现server端有client请求接入
            server = (ServerSocketChannel) selectionKey.channel();
            client = server.accept();
            client.configureBlocking(false);
//            生成的客户端对应的client接收从client传来的数据
            client.register(selector, SelectionKey.OP_READ);

        } else if(selectionKey.isReadable()) {
//            有client需要读入server
            client = (SocketChannel) selectionKey.channel();
            count = client.read(receiveBuffer);
            if(count > 0) {
//                从缓冲区取数据，成字符串
                receiveText = new String(receiveBuffer.array(), 0, count);
                System.out.println("server get data: " + receiveText);

//                注册写事件
                client.register(selector, SelectionKey.OP_WRITE);
            }

        } else if(selectionKey.isWritable()) {
//            清空发送缓存
            sendBuffer.clear();
            client = (SocketChannel) selectionKey.channel();
//            发送的数据
            sendText = "msg send to client:" + flag++;
            sendBuffer.put(sendText.getBytes());
            sendBuffer.flip();

            client.write(sendBuffer);
            System.out.println("server send data to client" + sendText);

        }

    }

    public static void main(String[] args) throws IOException {
        int port = 7080;
        NIOServer server = new NIOServer(port);
        server.listen();
    }
}
