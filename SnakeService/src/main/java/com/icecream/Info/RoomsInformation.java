package com.icecream.Info;


import java.util.Hashtable;

public class RoomsInformation {
    private Hashtable<String,RoomInfo> rooms=new Hashtable<String, RoomInfo>();

    //添加新的房间
    public void addRooms(String id,RoomInfo roomInfo){
        rooms.put(id,roomInfo);
    }

    //获得指定ID房间的所有信息
    public RoomInfo getRoomInfo(String id){
        return rooms.get(id);
    }

    public int getRoomsNum(){
        return rooms.size();
    }
    //重新设定特定ID的房间
    public void setRoomInfo(String id,RoomInfo roomInfo){
        rooms.remove(id);
        rooms.put(id,roomInfo);
    }

    //删除一个房间
    public void removeOneRoom(String id){
        rooms.remove(id);
    }
}
