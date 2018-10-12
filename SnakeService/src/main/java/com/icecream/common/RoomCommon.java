package com.icecream.common;

import com.icecream.Info.RoomsInformation;

public enum RoomCommon {
    ROOM_COMMON;

    private RoomsInformation roomsInformation;

    public void setRoomsInformation(RoomsInformation roomsInformation){
        this.roomsInformation=roomsInformation;
    }

    public RoomsInformation getRoomsInformation(){
        return roomsInformation;
    }
}
