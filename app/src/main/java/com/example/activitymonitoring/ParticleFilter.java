package com.example.activitymonitoring;

import java.util.Random;

public class ParticleFilter
{
    private final int PARTICLES = 7500;
    public Particle[] particles;

    ParticleFilter()
    {
        particles = new Particle[PARTICLES];
    }


    public int init()
    {
        Random init_pos = new Random();
        double weight = 1 / PARTICLES;




        return 0;
    }



}