package com.example.activitymonitoring;

public class Record
{
    double x[];
    double y[];
    double z[];
    int classLabel;
    int user;
    int timestamp;


    Record()
    {
        this.user = 0;
        this.timestamp = 0;
        this.classLabel = 0;
    }

    Record(double x[], double y[], double z[], int classLabel, int user, int timestamp)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.user = user;
        this.timestamp = timestamp;
        this.classLabel = classLabel;
    }

    static double clacDistanc(Record[] trainingsset, Record[] sample)
    {

        double distance = 0;

        for(int i = 0; i < sample.length; i ++)
        {
            distance += Math.pow(trainingsset.x - sample.x, 2);
        }
        distance = Math.sqrt(distance);

        return distance;
    }
    }
}

