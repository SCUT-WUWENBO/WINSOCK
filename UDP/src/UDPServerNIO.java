import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.*;

public class UDPServerNIO {

    String rawData;
    int num_len = 4;
    String localAddress, decimalTargetAddress, hexTargetAddress;
    int localPort, decimalTargetPort, hexTargetPort;

    // machine name and port
    String machineName = "";
    int machinePort = 0;

    // deviceID Map
    Map<String, String> deviceID = new HashMap<String, String>();
    int deviceNum = 0;

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

    //  write data into csv file
    public static void writeCsv(String savePath, String fileName, String time, String[] content) throws IOException {
        String filePath = savePath + "/" + fileName + ".csv";

        // create csv object
        File csvFile = new File(filePath);

        // if not exist dir, then create
        if (!csvFile.getParentFile().exists()) {
            csvFile.getParentFile().mkdirs();
        }
        // if not exist file, then create
        if (!csvFile.exists()) {
            csvFile.createNewFile();
        }

        try {
            // create writer object
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true));

            // write data in csv
            String str = "";
            for(int i = 0; i < content.length; i++) {
                str = str + content[i] + ",";
            }
            str += time;
            writer.write(str);
            writer.newLine();
            writer.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Not find csv file");
        } catch (IOException ex) {
            System.out.println("IO error");
        }
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

                    String ICCID = rawData.split("[#]")[0].trim();
                    String tempData = rawData.split("[#]")[1].trim();

                    // if first receive data from the device
                    if(!deviceID.containsKey(ICCID)) {
                        deviceID.put(ICCID, String.valueOf(++deviceNum));
                    }

                    // send hex message
                    sendBuffer.put(rawData.getBytes());
                    sendBuffer.flip();
                    channel.send(sendBuffer, new InetSocketAddress(this.hexTargetAddress, this.hexTargetPort + Integer.parseInt(deviceID.get(ICCID))));
                    sendBuffer.clear();

                    // send decimal message
                    writeCsv("./data", ICCID, strsystime, tempDecimal(tempData, num_len).split("\\s+"));
                    sendBuffer.put((ICCID + "# " + tempDecimal(tempData, num_len)).getBytes());
                    sendBuffer.flip();
                    channel.send(sendBuffer, new InetSocketAddress(this.decimalTargetAddress, this.decimalTargetPort + Integer.parseInt(deviceID.get(ICCID))));
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
        UDPServerNIO server = new UDPServerNIO("192.168.2.157", 10000);
        server.setDecimalTarget("192.168.2.157", 30000);
        server.setHexTarget("192.168.2.157", 20000);
        server.receive();
    }
}
