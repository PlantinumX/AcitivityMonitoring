package com.example.activitymonitoring;

import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;


public class MotionModel
{
    final int MAX_VALUES = 40;
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

    void calc_Motion(double values[], Record records, Motion motion)
    {
        motion.sample_cnt++;

    }


}
