package com.icecream.Info;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class SnakeInfo {
    public enum Dir {
        NORTH(1), SOUTH(-2), WEST(-1), EAST(2);

        public int dir;

        Dir(int dir) {
            this.dir = dir;
        }
    }

    public static class Coordinate {

        public int X, Y;

        public Coordinate(int x, int y) {
            this.X = x;
            this.Y = y;
        }
    }

    private Dir dir;
    private int style;
    private Coordinate headPos;
    private CopyOnWriteArrayList<Coordinate> body;

    //初始化蛇的位置
    public SnakeInfo() {
        Random random = new Random();
        this.dir = Dir.values()[random.nextInt(4)];
        this.style = random.nextInt(4);
        this.headPos = new Coordinate(10 + random.nextInt(20), 10 + random.nextInt(20));
        this.body = new CopyOnWriteArrayList<Coordinate>();
        body.add(new Coordinate(headPos.X, headPos.Y));
    }

    public Coordinate getHeadPos() {
        return new Coordinate(headPos.X, headPos.Y);
    }

    public Dir getDir() {
        return dir;
    }

    public Dir setDir(int d) {
        switch (d) {
            case 1:
                return Dir.NORTH;
            case 2:
                return Dir.EAST;
            case -1:
                return Dir.WEST;
            default:
                return Dir.SOUTH;
        }
    }

    public void setDir(Dir dir) {
        if (dir == null) {
            return;
        }
        this.dir = dir;
    }

    public void increase() {
        switch (dir) {
            case NORTH:
                headPos.Y -= 1;
                break;
            case SOUTH:
                headPos.Y += 1;
                break;
            case WEST:
                headPos.X -= 1;
                break;
            case EAST:
                headPos.X += 1;
                break;
        }
        if (body.size()<40) {
            body.add(new Coordinate(headPos.X, headPos.Y));
        }else {
            body.add(new Coordinate(headPos.X,headPos.Y));
            body.remove(0);
        }
    }

    public int getStyle() {
        return style;
    }

    public List<Coordinate> getBody() {
        return body;
    }

    //判断是否咬到自己
    public boolean isContainWithoutHead(Coordinate headPos) {
        for (int i = 0; i < body.size() - 1; i++) {
            Coordinate coordinate = body.get(i);
            if (headPos.X == coordinate.X && headPos.Y == coordinate.Y) {
                return true;
            }
        }
        return false;
    }


    public boolean isContain(Coordinate headPos) {
        for (Coordinate coordinate : body) {
            if (headPos.X == coordinate.X && headPos.Y == coordinate.Y) {
                return true;
            }
        }
        return false;
    }
}
