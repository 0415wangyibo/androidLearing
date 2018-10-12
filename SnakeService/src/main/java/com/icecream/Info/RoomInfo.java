package com.icecream.Info;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
public class RoomInfo {
    public static final int SIDE = 40;
    private Hashtable<String,SnakeInfo> snakes = new Hashtable<String, SnakeInfo>();

    public void addSnake(String id) {
        snakes.put(id, new SnakeInfo());
    }

    public void addSnake(String id,SnakeInfo snakeInfo){
        snakes.put(id,snakeInfo);
    }
    public void reomveSnake(String id) {
        snakes.remove(id);
    }

    public void update() {
        for (SnakeInfo snake : snakes.values()) {
            snake.increase();
        }
    }

    public int getPlayerNum() {
        return snakes.size();
    }

    public Set<String> getIdSet() {
        return snakes.keySet();
    }

    public SnakeInfo getSnakeInfo(String id) {
        return snakes.get(id);
    }

    //获得死亡的蛇的ID
    public List<String> checkDetection() {
        List<String> result = new LinkedList<String>();

        for (String id : snakes.keySet()) {
            SnakeInfo.Coordinate headPos = snakes.get(id).getHeadPos();

            //判断蛇是否撞到边界
            if (headPos.X <= 0 || headPos.X >= SIDE - 1 || headPos.Y <= 0 || headPos.Y >= SIDE - 1) {
                result.add(id);
                continue;
            }


            for (String testId : snakes.keySet()) {
                // 判断自己是否咬到自己
                if (id.equals(testId)) {
                    if (snakes.get(testId).isContainWithoutHead(headPos)) {
                        result.add(id);
                        break;
                    }
                }
                //判断是否咬到其它蛇
                else {
                    if (snakes.get(testId).isContain(headPos)) {
                        result.add(id);
                        break;
                    }
                }
            }
        }

        return result;
    }
}
