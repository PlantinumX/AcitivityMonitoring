package com.example.activitymonitoring;

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
        double weight = 1 / PARTICLES;

        return 0;
    }



}