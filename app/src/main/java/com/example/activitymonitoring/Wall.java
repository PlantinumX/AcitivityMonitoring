package com.example.activitymonitoring;


import android.graphics.*;

import java.util.Random;

public class Wall {
    public Position top_left;
    public Position top_right;
    public Position bottom_right;
    public Position bottom_left;
    public int color;
    public Wall() {
        this.top_left = new Position();
        this.top_right = new Position();
        this.bottom_left = new Position();
        this.bottom_right = new Position();

    }

    public Wall(Position top_left,Position top_right,Position bottom_left,Position bottom_right) {
        this.top_left = top_left;
        this.top_right = top_right;
        this.bottom_left = bottom_left;
        this.bottom_right = bottom_right;
        this.color = new Random().nextInt() + (int)Math.round(bottom_left.getX());
    }


    public Wall(Wall wall) {
        this.top_left = wall.top_left;
        this.top_right = wall.top_right;
        this.bottom_left = wall.bottom_left;
        this.bottom_right = wall.bottom_right;


    }

    public boolean checkInBoundary(int x, int y) {
        return x >= top_left.getX() && y >= top_left.getY() && x <= bottom_right.getX() && y <= bottom_right.getY();
    }

}