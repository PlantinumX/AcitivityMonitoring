package com.example.activitymonitoring;

public class Pixel
{
    private int x;
    private int y;
    private boolean used;

    public Pixel(int x, int y, boolean used)
    {
        this.x = x;
        this.y = y;
        this.used = used;
    }

    public Pixel()
    {
        this.x = 0;
        this.y = 0;
        this.used = false;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
