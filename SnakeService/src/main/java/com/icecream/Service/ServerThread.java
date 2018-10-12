package com.icecream.Service;

import com.icecream.Info.MessageInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

import com.google.gson.Gson;
import com.icecream.Info.RoomInfo;
import com.icecream.Info.RoomsInformation;
import com.icecream.Info.SnakeInfo;
import com.icecream.common.RoomCommon;

public class ServerThread extends Thread {

    private Socket socket;
    private volatile boolean isRunning = true;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String roomID="";
    private String snakeID="";
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Gson gson = new Gson();
    private RoomsInformation roomsInformation;
    private SnakeInfo snakeInfo;
    private RoomInfo roomInfo;
    private boolean isGaming=true;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        listen();
    }

    //监听来自客户端的消息
    private void listen() {
        executorService.execute(new Runnable() {

            public void run() {
                try {
                    while (isRunning) {
                        String data = reader.readLine();
                        if (data == null) {
                            break;
                        }
                        MessageInfo msg = gson.fromJson(data, MessageInfo.class);
                        if (msg == null) {
                            break;
                        }
                        System.out.println(msg.flag + ":" + msg.info);
                        if ("login".equals(msg.flag)){
                            isGaming=true;
                            doLogin(msg.info);
                        }
                        if ("createRoom".equals(msg.flag)){
                            isGaming=true;
                            doCreateRoom(msg.info);
                        }
                        if ("joinRoom".equals(msg.flag)){
                            isGaming=true;
                            doJoinRoom(msg.info);
                        }
                        if ("gameStart".equals(msg.flag)){
                            doGameStart();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doLogin(String ID){
        snakeID= ID;
    }

    private void doCreateRoom(String ID){
        roomsInformation= RoomCommon.ROOM_COMMON.getRoomsInformation();
        if (null==roomsInformation||null==roomsInformation.getRoomInfo(ID)){
            roomInfo=new RoomInfo();
            snakeInfo=new SnakeInfo();
            System.out.println(gson.toJson(snakeInfo));
            roomInfo.addSnake(snakeID,snakeInfo);
            roomsInformation.addRooms(ID,roomInfo);
            RoomCommon.ROOM_COMMON.setRoomsInformation(roomsInformation);
            roomID=ID;
            sendInfo("gameStart","");
        }else {
            sendInfo("roomExists","");
        }
    }

    private void doJoinRoom(String ID){
        roomsInformation=RoomCommon.ROOM_COMMON.getRoomsInformation();
        System.out.println(gson.toJson(roomsInformation));
        System.out.println(null!=roomsInformation);
        System.out.println(roomsInformation.getRoomsNum()>0);
        System.out.println(null!=roomsInformation.getRoomInfo(ID));
        if ((null!=roomsInformation)&&(roomsInformation.getRoomsNum()>0)&&(null!=roomsInformation.getRoomInfo(ID))){
            roomID=ID;
            roomInfo=roomsInformation.getRoomInfo(ID);
            snakeInfo=new SnakeInfo();
            System.out.println(gson.toJson(snakeInfo));
            roomInfo.addSnake(snakeID,snakeInfo);
            roomsInformation.setRoomInfo(ID,roomInfo);
            RoomCommon.ROOM_COMMON.setRoomsInformation(roomsInformation);
            sendInfo("gameStart","");
        }
        else {
            System.out.println("doing else");
            sendInfo("roomNotExist","");
        }
    }

    private void doGameStart(){
        roomsInformation=RoomCommon.ROOM_COMMON.getRoomsInformation();
        roomInfo=roomsInformation.getRoomInfo(roomID);
        snakeInfo=roomInfo.getSnakeInfo(snakeID);
        System.out.println(gson.toJson(snakeInfo));
         executorService.execute(new Runnable() {
             public void run() {
                 while (isGaming) {
                     try {
                         String data=reader.readLine();
                         if (data == null) {
                             break;
                         }
                         MessageInfo msg = gson.fromJson(data, MessageInfo.class);
                         if (msg == null) {
                             break;
                         }
                         if ("move".equals(msg.flag)){
                             SnakeInfo.Dir dir=gson.fromJson(msg.info,SnakeInfo.Dir.class);
                             snakeInfo.setDir(dir);
                         }
                         if ("quit".equals(msg.flag)){
                             isGaming=false;
                             break;
                         }
                     } catch (IOException e) {
                         e.printStackTrace();
                     }

                 }
             }
         });

         executorService.execute(new Runnable() {
             public void run() {
                 while (isGaming) {
                     snakeInfo.increase();
                     String info=gson.toJson(roomInfo);
                     sendInfo("roomData",info);
                     System.out.println(info);
                     final List<String> result = roomInfo.checkDetection();
                     if (result.size()>0&&result.contains(snakeID)){
                         sendInfo("gameOver","");
                         isGaming=false;
                         roomInfo.reomveSnake(snakeID);
                         if (roomInfo.getPlayerNum()==0){
                             roomsInformation.removeOneRoom(roomID);
                         }
                     }
                     RoomCommon.ROOM_COMMON.setRoomsInformation(roomsInformation);
                     try {
                         Thread.sleep(300);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }
             }
         });
    }

    //向客户端发送相关信息
    private void sendInfo(final String flag, final String info){
        executorService.execute(new Runnable() {
            public void run() {
                String msg = gson.toJson(new MessageInfo(flag, info));
                try {
                    writer.write(msg);
                    writer.newLine();
                    writer.flush();
                    System.out.println("doing sendInfo");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
