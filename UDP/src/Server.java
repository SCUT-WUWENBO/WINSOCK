//Server.java

import java.io.IOException;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try
        {
//            create socket
            DatagramSocket server = new DatagramSocket(5060);
//            packet Data(Data and Data valid length)
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

//            target port
            packet.setPort(5070);
            packet.setAddress(InetAddress.getLocalHost());

//            receive data
            server.receive(packet);
            System.out.println(packet.getAddress().getHostName() + "(" + packet.getPort() + "):" + new String(packet.getData()));

//            send data
            packet.setData("Hello Client".getBytes());
            server.send(packet);

//            close socket
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
