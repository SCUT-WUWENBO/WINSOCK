//Client.java

import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try
        {
//            create socket
            DatagramSocket client = new DatagramSocket(5070);
//            packet Data(Data and Data valid length)
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

//            target port
            packet.setPort(5060);
            packet.setAddress(InetAddress.getLocalHost());

//            send data
            packet.setData("Hello Server".getBytes());
            client.send(packet);

//            receive data
            client.receive(packet);
            System.out.println(packet.getAddress().getHostName() + "(" + packet.getPort() + "):" + new String(packet.getData()));

//            close socket
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
