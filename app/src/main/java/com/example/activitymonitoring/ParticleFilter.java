package com.example.activitymonitoring;

public class ParticleFilter
{
    public final int PARTICLES = 7500;
    public Particle[] particles;

    ParticleFilter()
    {
        particles = new Particle[PARTICLES];
    }
}
