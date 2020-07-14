import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class AIOServer {

//    init
    public AIOServer(int port) throws IOException {
//        open channel and bind port
        final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));

//        listen the connection
        listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

//            complete data transmission
            @Override
            public void completed(AsynchronousSocketChannel ch, Void vi) {
//                listen next connection
                listener.accept(null, this);
                System.out.println("new connection");
                handler(ch);
            }

//            fail data transmission
            @Override
            public void failed(Throwable exc, Void vi) {
                System.out.println("异步IO失败");
            }
        });
    }

//    handler function  <-- work place -->
    public void handler(AsynchronousSocketChannel ch) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        try {
            ch.read(byteBuffer).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        byteBuffer.flip();
        System.out.println("server receive data: " + byteBuffer.get());

    }

    public static void main(String[] args) throws Exception {
        int port = 7080;
        AIOServer server = new AIOServer(port);
        System.out.println("server listen port: " + port);
        Thread.sleep(100000);
    }
}
