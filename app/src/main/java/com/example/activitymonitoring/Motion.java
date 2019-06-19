package com.example.activitymonitoring;

import java.util.ArrayList;

public class Motion {
    public ArrayList<Double> angle;
    public Long duration;
    int sample_cnt;

    public Motion() {
        this.sample_cnt = 0;
        this.angle = new ArrayList<>();
        this.duration = (long) 0;
    }


}
