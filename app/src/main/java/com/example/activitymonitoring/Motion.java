package com.example.activitymonitoring;

import java.util.ArrayList;

public class Motion
{
    public ArrayList<Boolean> is_moving;
    public ArrayList<Double> angle;
    public ArrayList<Long> duration;

    public Motion(boolean is_moving, double angle)
    {
        this.is_moving = new ArrayList<>();
        this.angle = new ArrayList<>();
        this.duration = new ArrayList<>();
    }

    public Motion()
    {
        this.is_moving = new ArrayList<>();
        this.angle = new ArrayList<>();
    }



}
