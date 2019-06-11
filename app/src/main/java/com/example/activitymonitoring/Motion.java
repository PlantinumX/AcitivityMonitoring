package com.example.activitymonitoring;

public class Motion
{
    private boolean is_moving;
    private  double angle;

    public Motion(boolean is_moving, double angle)
    {
        this.is_moving = is_moving;
        this.angle = angle;
    }

    public boolean getIsMoving()
    {
        return is_moving;
    }

    public void setIsMoving(boolean is_moving)
    {
        this.is_moving = is_moving;
    }

    public double getAngle()
    {
        return angle;
    }

    public void setAngle(double angle)
    {
        this.angle = angle;
    }

}
