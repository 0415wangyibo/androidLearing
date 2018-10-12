package com.icecream.Service;
import com.icecream.Info.RoomsInformation;
import com.icecream.common.RoomCommon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class MainService {
    public static void main(String[] args){
        try {
            ServerSocket serverSocket=new ServerSocket(20480);
            Socket socket=null;
            RoomCommon.ROOM_COMMON.setRoomsInformation(new RoomsInformation());
            while(true){
                //调用accept()方法开始监听，等待客户端的连接
                socket=serverSocket.accept();
                //创建一个新的线程
                ServerThread serverThread=new ServerThread(socket);
                //启动线程
                serverThread.start();

                InetAddress address=socket.getInetAddress();
                System.out.println("当前客户端的IP："+address.getHostAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
