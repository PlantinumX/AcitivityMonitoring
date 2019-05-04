package com.example.activitymonitoring;

public class Record
{
    double x;
    double y;
    double z;
    double distance;
    int classLabel;


    Record()
    {
        this.user = 0;
        this.timestamp = 0;
        this.classLabel = 0;
    }

    Record(double x, double y, double z, int classLabel, int user, int timestamp)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.classLabel = classLabel;
    }

    void clacDistanc(Record trainingsset, Record sample)
    {


        for(int i = 0; i < sample.length; i ++)
        {
            this.distance += Math.pow(trainingsset.x - sample.x, 2);
        }
        this.distance = Math.sqrt(distance);

    }

}

