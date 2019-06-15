package com.example.activitymonitoring;

public class Position {
    public double x;
    public double y;


    public Position() {
        this.x = 0;
        this.y = 0;
    }

    public Position(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }
}


