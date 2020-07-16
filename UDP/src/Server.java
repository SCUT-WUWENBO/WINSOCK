//Server.java

import java.io.IOException;
import java.net.*;

public class Server {
    //将16进制的字符串转成字符数组
    public static byte[] getHexBytes(String str){
        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    public static void main(String[] args) {
        try
        {
//            create socket
            DatagramSocket server = new DatagramSocket(10000);
//            packet Data(Data and Data valid length)
            DatagramPacket packet_receive = new DatagramPacket(new byte[1024], 1024);
            DatagramPacket packet_send =  new DatagramPacket(new byte[1024], 1024);
            String receiveMessage = null;

//            data processed
            String [] processedMessage = null;
            String ICCID = "";
            String data = "";
            String processedData = "";
            int num_len = 4;

//            machine name and port
            String machineName = "";
            int machinePort = 0;

//                target port
//            packet_send.setPort(12000);


            while (true) {
                packet_send.setAddress(InetAddress.getByName("192.168.2.109"));

//            receive data
                server.receive(packet_receive);

                receiveMessage = new String(packet_receive.getData(), 0, packet_receive.getLength());
                System.out.println(packet_receive.getAddress().getHostName() + "(" + packet_receive.getPort() + "):" + receiveMessage);

//                isCommand
                if (receiveMessage.indexOf("#") == -1) {
                    packet_send.setAddress(InetAddress.getByName(machineName));
                    packet_send.setPort(machinePort);


                    packet_send.setData(getHexBytes(receiveMessage));
                    server.send(packet_send);
                    continue;
                } else if (receiveMessage.split("[#]")[1].trim().length() != num_len * 8) {
//                    is command Return

//                    packet_send.setPort(12000);
//                    packet_send.setData(receiveMessage.getBytes());
//                    server.send(packet_send);
//
//                    packet_send.setPort(13000);
//                    packet_send.setData(receiveMessage.getBytes());
//                    server.send(packet_send);
                    continue;
                }

//                send raw data
                System.out.println("Send Raw Data!!");


                packet_send.setPort(12000);
                packet_send.setData(receiveMessage.getBytes());
                server.send(packet_send);


                System.out.println("Send Processed Data!!");
                processedData = "";
                processedMessage = receiveMessage.split("[#]");

                ICCID = processedMessage[0].trim();
                data = processedMessage[1].trim();

                for(int x = 0; x < data.length() / num_len; x++) {
                    String hex = data.substring(num_len * x, num_len * (x + 1));
                    int value = 0;

                    //  即符号位为1
                    if (Integer.parseInt(hex.substring(0, 1), 16) >= 8) {
                        value = Integer.parseInt(hex,16) - Integer.parseInt("10000",16);
                    } else {
                        value = Integer.parseInt(hex,16);
                    }

                    processedData = processedData + String.format("%.1f  ", value * 0.1);
                }

                receiveMessage = ICCID + "# " + processedData;

                packet_send.setPort(13000);
                packet_send.setData(receiveMessage.getBytes());
                server.send(packet_send);

                machineName = packet_receive.getAddress().getHostName();
                machinePort = packet_receive.getPort();


            }
//            close socket
//            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
