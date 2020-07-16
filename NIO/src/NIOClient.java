import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOClient {

    private static int flag = 1;

//    private static int blockSize = 4096;
    private static ByteBuffer sendBuffer = ByteBuffer.allocate(4096);
    private static ByteBuffer receiveBuffer = ByteBuffer.allocate(4096);

//    server地址
    private final static InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 7080);

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
//        open selector
        Selector selector = Selector.open();
//        register connect
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(serverAddress);

//        declare
        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey> iterator;
        SelectionKey selectionKey;
        SocketChannel client;
        String receiveText;
        String sendText;
        int count;

        while(true) {
            selectionKeys = selector.selectedKeys();
            iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                selectionKey = iterator.next();
                if(selectionKey.isConnectable()) {
                    System.out.println("client connect");
                    client = (SocketChannel) selectionKey.channel();

//                    确定完成连接
                    if(client.isConnectionPending()) {
                        client.finishConnect();
                        System.out.println("客户端完成连接");
                        sendBuffer.clear();
                        sendBuffer.put("Hello, Server".getBytes());
                        sendBuffer.flip();
                        client.write(sendBuffer);

                    }
                    client.register(selector, SelectionKey.OP_READ);
                } else if(selectionKey.isReadable()) {
                    client = (SocketChannel) selectionKey.channel();
                    receiveBuffer.clear();
                    count = client.read(receiveBuffer);
                    if(count > 0) {
                        receiveText = new String(receiveBuffer.array(), 0, count);
                        System.out.println("client receive: " + receiveText);

                        client.register(selector, SelectionKey.OP_WRITE);
                    }

                } else if(selectionKey.isWritable()) {
                    sendBuffer.clear();
                    client = (SocketChannel) selectionKey.channel();
                    sendText = "Msg send to Server: " + flag++;
                    sendBuffer.put(sendText.getBytes());
                    sendBuffer.flip();
                    client.write(sendBuffer);

                    System.out.println("client send to server: " + sendText);
                    client.register(selector, SelectionKey.OP_READ);
                }
            }
            selectionKeys.clear();
        }
    }
}
