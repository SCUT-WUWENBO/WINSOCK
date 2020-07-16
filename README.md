# WINSOCK
## UDP
> DatagramSocket and DatagramPacket(类似码头和货轮)

1. create socket(local port)
2. packet Data(Data and Data valid length)
3. Set target port
4. send data and receive data
5. close socket

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

## NIO and AIO
### 阻塞和非阻塞
阻塞和非阻塞是进程在访问数据的时候，数据是否准备就绪的一种处理方式。当数据没有准备好
- 阻塞：往往需要等待缓冲区中的数据准备好过后才处理其他的事情，否则一直等待
- 非阻塞：当我们的进程访问我们的数据缓冲区的时候，，数据没有准备好的时候，直接返回，不需要等待。有数据的时候，也直接返回
### 同步和异步
同步和异步都是基于应用程序和操作系统处理IO事件所采用的方式。
- 同步：应用程序要直接参与IO读写的操作。同步的方式在处理IO事件的时候，必须阻塞在某个方法上面等待我们的IO事件完成（阻塞IO事件或通过轮询IO事件的方式）
- 异步：所有的IO读写交给操作系统去处理。这个时候，我们可以去做其他的事情。并不需要去完成真正的IO操作，当操作完成后，给我们的应用程序一个通知就可以了

同步：（1）阻塞到IO事件，阻塞到read或者wrtie。这个时候我们就完全不能做自己的事情，让读写方法加入到线程里面，然后阻塞线程，对线程的性能开销大
（2）IO事件的轮询：多入复用技术(select模式)。将读写事件交给一个单独的线程，这个线程完成IO事件的注册功能，还有就是不断地去轮询我们的读写缓冲区，看是否有数据准备好。通知相应的读写线程。这样的话，以前的读写线程就可以做其他的事情，这个时候阻塞的不是所有的IO线程，阻塞的是select线程。

### Java IO模型
1. BIO：阻塞IO。阻塞到我们的读写方法，阻塞到线程来提供性能，对于线程的开销本来就是性能的浪费
2. NIO：linux多路复用技术(select模式)。实现IO事件的轮询方式，同步非阻塞模式
3. AIO：实现真正的异步AIO，学习linux epoll模式

### NIO/AIO原理
NIO/AIO并没有改变网络通信的基本步骤，只是在原来的基础上(serverSocket,socket)上做了改进，三次握手开销较大，对读写通信管道进行抽象。

### 对于读和写采用抽象的管道的概念
Channel：Channel是在一个TCP链接之间抽象，一个TCP链接可以对应多个管道，而不是以前的方式只有一个通信信道，减少了TCP链接的次数
UDP：采用相同的方式，抽象成管道

### NIO原理
通过selector(选择器)充当管家，管理所有的IO事件(Connection,accept,client和server的读写)

### Selector如何进行管理IO事件
当IO事件注册给selector时，selector会给他们分配一个key(可以理解成一个事件的标签)。当IO事件完成，通过key值来找到相应的管道，然后通过管道发送数据和接收数据

### 数据缓冲区
通过bytebuffer类，提供很多读写的方法put(), get()

- 服务端：ServerSocketChannel
- 客户端：SocketChannel
- 选择器：Selector selector=Selector.open();这样打开选择器
- Selectionkey：可以通过它来判断IO事件是否已经就绪
- key.isAcceptable,key.conectionable,key.isreadble(),key.iswriteable()

### 如何获得事件的keys
Selectionkey keys = Selector.selectedkeys();

### 如何注册(监听IO事件)
- channel.register(Selector,Selectionkey.OP_Write);
- channel.register(Selector,Selectionkey.OP_Read);
- channel.register(Selector,Selectionkey.OP_Connect);
- channel.register(Selector,Selectionkey.OP_Accept);

### AIO
- 服务端：AsynchronousServerSocketChannel
- 客户端：AsynchronousSocketChannel
- 用户处理器：CompletionHandler接口，这个接口实现应用程序向操作系统发起IO请求，当完成后处理具体逻辑，否则做自己该做的事

#### AIO实例
实现服务器监听客户端，并且接收客户端发过来的数据

#### NIO实例
实现客户端和服务端的通信

## 使用方法
### UDP
**在Send data前需要设定target**
```
javac Server.java
javac Client.java

java Client
java Server
```
### TCP
```
javac GreetingServer.java
javac GreetingClient.java

java GreetingServer
java GreetingClient
```
### AIO
```
javac AIOServer.java
javac AIOClient.java

java AIOServer
java AIOClient
```

### NIO
实现出了一点问题：服务器端可以监听，但客户端发送connect请求，未连接上；待后续解决

## 目前UDP功能
1. 可以将传感器的数据按照补码形式或者十进制显示
2. 可以将输入的字符串转化为16进制的命令发送给最近的传感器
3. 可以完成中转功能，将数据通过UDP转发给其他需要接收数据的端口，可以跨公网