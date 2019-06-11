package com.example.activitymonitoring;

import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;


public class MotionModel
{
    final int MAX_VALUES = 40;
    private int steps;
    private double degree;
    private ArrayList<Double> xAcc = new ArrayList<Double>();
    private ArrayList<Double> yAcc = new ArrayList<Double>();
    private ArrayList<Double> zAcc = new ArrayList<Double>();

    double mean(ArrayList<Double> values)
    {
        double mean_value = 0;
        for(int i = 0; i < values.size(); i++)
        {
            mean_value += values.get(i);
        }
        mean_value /= values.size();

        return mean_value;
    }

    double variance (ArrayList<Double> values)
    {
        double mean = mean(values);
        double variance = 0;

        for(int i = 0; i < values.size(); i++)
        {
             variance += Math.pow(values.get(i) - mean,2);

        }
        variance /= mean;
        return variance;
    }

    void calc_Motion(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            xAcc.add((double)event.values[0]);
            yAcc.add((double)event.values[1]);
            zAcc.add((double)event.values[2]);
        }

        if(xAcc.size() == MAX_VALUES && yAcc.size() == MAX_VALUES && zAcc.size() == MAX_VALUES)
        {


        }


    }


}
