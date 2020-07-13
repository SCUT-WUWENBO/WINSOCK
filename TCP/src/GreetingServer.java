//GreetingServer.java

import java.net.*;
import java.io.*;

//$ javac GreetingServer.java
//$ java GreetingServer 6066

public class GreetingServer extends Thread {
//    declare serverSocket
    private ServerSocket serverSocket;

//    initial
    public GreetingServer(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
//        timeout is 10000ms
        serverSocket.setSoTimeout(10000);
    }


    public void run()
    {
//        if one loop is executed successfully, then server will continue to do next loop
//        every time set a socket, response to corresponding client
        while(true)
        {
            try
            {
//                print local port
                System.out.println("wait for client, server port is: " + serverSocket.getLocalPort() + "...");
//                wait for connection from client(return a new socket connected to client)
                Socket server = serverSocket.accept();
//                print client address
                System.out.println("client address is: " + server.getRemoteSocketAddress());

                DataInputStream in = new DataInputStream(server.getInputStream());
                System.out.println(in.readUTF());

                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF("Thanks for connect to me: " + server.getLocalSocketAddress() + "\nGoodbye!");

//                close socket
                server.close();
            }catch (SocketTimeoutException s)
            {
                System.out.println("Socket time out!");
                break;
            }catch (IOException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try
        {
            Thread t = new GreetingServer(port);
            t.run();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
