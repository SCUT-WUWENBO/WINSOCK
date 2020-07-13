# WINSOCK
## UDP
> DatagramSocket and DatagramPacket(类似码头和货轮)

1. create socket(local port)
2. packet Data(Data and Data valid length)
3. Set target port
4. send data and receive data
5. close socket

---

## TCP

- Server(Multi-threading)
    1. declare and create new ServerSocket
    2. wait for connection from client(return a new socket connected to client)
    3. output interface and input interface(send and receive data)
    4. close socket
- Client
    1. create socket and connect to server
    2. output interface and input interface(send and receive data)
    3. close socket

    