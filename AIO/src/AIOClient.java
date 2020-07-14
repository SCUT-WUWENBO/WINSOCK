import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AIOClient {

//    declare
    private AsynchronousSocketChannel client = null;

//    init
    public AIOClient(String host, int port) throws IOException, InterruptedException, ExecutionException {
//        open channel
        client = AsynchronousSocketChannel.open();

        Future<?> future = client.connect(new InetSocketAddress(host, port));
        System.out.println(future.get());

    }

    public void write(byte b) {
//        create dataBuffer and writeIn data
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        byteBuffer.put(b);
        byteBuffer.flip();

        client.write(byteBuffer);
    }

    public void printMsg() throws IOException {
        System.out.println(client.getLocalAddress());
        System.out.println(client.getRemoteAddress());
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException{
//        create the object
        AIOClient client = new AIOClient("localhost", 7080);
        client.printMsg();
        client.write((byte)11);
    }
}
