import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.text.SimpleDateFormat;
import java.util.Iterator;

public class UDPServerNIO {

    String rawData;
    int num_len = 4;
    String localAddress, decimalTargetAddress, hexTargetAddress;
    int localPort, decimalTargetPort, hexTargetPort;

    // machine name and port
    String machineName = "";
    int machinePort = 0;

    // init
    public UDPServerNIO (String address, int port) {
        this.localPort = port;
        this.localAddress = address;
    }

    //get hex bytes
    public static byte[] getHexBytes(String str){
        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    public String tempDecimal (String tempData, int num_len) {
        StringBuilder processedDecimal = new StringBuilder();
        for(int x = 0; x < tempData.length() / num_len; x++) {
            String hex = tempData.substring(num_len * x, num_len * (x + 1));
            int value;

            if (Integer.parseInt(hex.substring(0, 1), 16) >= 8) {
                value = Integer.parseInt(hex,16) - Integer.parseInt("10000",16);
            } else {
                value = Integer.parseInt(hex,16);
            }

            processedDecimal.append(String.format("%.1f  ", value * 0.1));
        }

        return processedDecimal.toString();
    }

    public void setDecimalTarget(String address, int port) {
        this.decimalTargetAddress = address;
        this.decimalTargetPort = port;
    }

    public void setHexTarget(String address, int port) {
        this.hexTargetAddress = address;
        this.hexTargetPort = port;
    }

    // forward data module
    public void receive() throws IOException {
        final DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(this.localAddress, this.localPort));
        System.out.println("UDP server start!!!");

        final Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        long num = 0;

        // query for IO event by selector
        while (selector.select() > 0) {
            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            final ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
            final ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
            while (iterator.hasNext()) {
                final SelectionKey selectionKey = iterator.next();
                // isReadable event
                if (selectionKey.isReadable()) {
                    final SocketAddress client = channel.receive(receiveBuffer);
                    System.out.println(++num);
                    receiveBuffer.flip();
                    rawData = new String(receiveBuffer.array());

                    SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strsystime = sf.format(System.currentTimeMillis());
                    System.out.println(strsystime + " received data: " + rawData);

                    // is Command
                    if(!rawData.contains("#")) {
//                        sendBuffer.put(getHexBytes(rawData));
//                        sendBuffer.flip();
//                        channel.send(sendBuffer, new InetSocketAddress("192.168.2.119", 12000));
//                        sendBuffer.clear();
                        System.out.println(rawData);
                        continue;
                    } else if (rawData.split("[#]")[1].trim().length() != num_len * 8) {
                        // is Command return
                        System.out.println(rawData);
                        continue;
                    }

                    // send hex message
                    sendBuffer.put(rawData.getBytes());
                    sendBuffer.flip();
                    channel.send(sendBuffer, new InetSocketAddress(this.hexTargetAddress, this.hexTargetPort));
                    sendBuffer.clear();

                    // send decimal message
                    String ICCID = rawData.split("[#]")[0].trim();
                    String tempData = rawData.split("[#]")[1].trim();

                    sendBuffer.put((ICCID + "# " +tempDecimal(tempData, num_len)).getBytes());
                    sendBuffer.flip();
                    channel.send(sendBuffer, new InetSocketAddress(this.decimalTargetAddress, this.decimalTargetPort));
                    sendBuffer.clear();

                    // record lately client
                    machineName = client.toString().split("[:]")[0].trim().substring(1);
                    machinePort = Integer.parseInt(client.toString().split("[:]")[1].trim());
                }
            }
            iterator.remove();
        }
        selector.close();
        channel.close();
    }

    public static void main(String[] args) throws IOException {
        UDPServerNIO server = new UDPServerNIO("192.168.2.119", 10000);
        server.setDecimalTarget("192.168.2.119", 12000);
        server.setHexTarget("192.168.2.119", 13000);
        server.receive();
    }
}
