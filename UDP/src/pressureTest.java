import java.io.IOException;
import java.net.*;

// 1000 ports send message to server every second, last for 600 seconds

class ThreadDemo extends Thread {
    private Thread t;
    private final int port;

    ThreadDemo(int port) {
        this.port = port;
    }

    public void run() {
        try {
            DatagramSocket server = new DatagramSocket(port);
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            // last for 600 seconds
            for (int i=0; i< 600;i++) {
                // send UDP packet to server
                packet.setData("460043338905390# FFF5FFF0FFF1FFF2FFF6FFEF00C100B9".getBytes());
                packet.setPort(10000);
                packet.setAddress(InetAddress.getLocalHost());
                server.send(packet);

                Thread.sleep(1000); // wait for 1000 ms
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void start () {
        if (t == null) {
            t = new Thread (this, String.valueOf(port));
            t.start ();
        }
    }
}

public class pressureTest {
    public static void main(String[] args) {
        //  Set 1000 ports, from 14000
        for(int i=0;i<1000;i++) {
            new ThreadDemo( 14000+i).start();
        }
    }
}