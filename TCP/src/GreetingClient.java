//GreetingClient.java

import java.net.*;
import java.io.*;

//$ javac GreetingClient.java
//$ java GreetingClient localhost 6066

public class GreetingClient {
    public static void main(String[] args) {
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        try {
//            print serverName and port
            System.out.println("Connect to host:" + serverName + " , port is: " + port);
//            connect to server
            Socket client = new Socket(serverName, port);
            System.out.println("server address is: " + client.getRemoteSocketAddress());

//            output interface
//            output some data to server/get response from server
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("Hello from " + client.getLocalSocketAddress());

//            input some data from server
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Server response: " + in.readUTF());

//            close socket
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
