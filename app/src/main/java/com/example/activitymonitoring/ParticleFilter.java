package com.example.activitymonitoring;


import android.graphics.Bitmap;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;

import java.util.Random;

public class ParticleFilter
{
    private final int PARTICLES = 15000; //AMOUNT OF PARTICLES
    public Particle[] particles;
    public Map map;
    ParticleFilter(Map map)
    {
        particles = new Particle[PARTICLES];
        this.map = map;
        init();
    }


    public int init()
    {
        double weight = 1 / PARTICLES; //first
        initParticlesIntoMap(weight);


        return 0;
    }

    void meterToPixelConverter(double distance) {


    }

    //TODO move particles gives me direction and distance
    public void moveParticles(double distance,double direction) //mobile phone detected movement calculated distance we got stride + directioon
    {
        meterToPixelConverter(distance);
        direction = Math.toRadians(direction);
        for(Particle particle : particles)
        {
            Position position = particle.getPos();
            particle.setLastPos(position);
            Position newPosition = new Position();
            newPosition.setX((int) (position.getX() + distance * Math.cos(direction)));//TODO WE MUST DO SOMETHIG ABOUT NOISE
            newPosition.setY((int) (position.getX() + distance * Math.sin(direction)));//TODO WE MUST DO SOMETHING ABOUT NOISE
            checkParticles();
            low_variance_resampling();
        }
    }

    //OOM not today
    //https://github.com/JuliaStats/StatsBase.jl/issues/124 looked into this code
    public boolean low_variance_resampling() //calculate new weights but how
    {
        Particle[] resampled_particles = new Particle[PARTICLES];
        double r = new Random().nextDouble();
        double c = this.particles[0].getWeight();
        int i = 1;
        for(int m = 1;m < PARTICLES;m++)
        {
            double U = r + (double) (i - 1) / PARTICLES;
            while (c < U) {
                i++;
                c += particles[m].getWeight();
            }
            resampled_particles[m] = new Particle(particles[i]);

        }
        //generate 
        return true;
    }

    //TODO hardcodde walls if there is time
    //we should do a line and check if line is intersectin
    public void checkParticles() //our map is a bitmap what we are doing is to check threshold +15-15 pixels in bitmap if it is intersecting with the wall
    {
        for(Particle particle : particles)
        {
            Position position = particle.getPos();
            Position lastPosition = particle.getLastPos();
            Bitmap floorplan = this.map.getOriginal_image();
            boolean stopIteration = false;


        }
    }

//https://stackoverflow.com/questions/5184815/java-intersection-point-of-a-polygon-and-line
    public  static float[] intersect(float lx1, float ly1, float lx2, float ly2,
                                     float px1, float py1, float px2, float py2) {
        float ml,mp;
        // calc slope
    if(lx1-lx2 == 0) {

         ml = (ly1-ly2) / (1);
    }else{
        ml = (ly1-ly2) / (lx1 - lx2);
    }
    if(px1 - px2 == 0) {

        mp = (py1-py2) / (1);
    }
    else {
        mp = (py1-py2) / (px1 - px2);

    }

    // calc intercept
    float bl = ly1 - (ml*lx1);
    float bp = py1 - (mp*px1);

    float x = (bp - bl) / (ml - mp);
    float y = ml * x + bl;
    return new float[]{x,y};
}

    void initParticlesIntoMap(double initialweights) {
//        Random xAxis = new Random();
//        Random yAxis = new Random();
        Random rand = new Random();
        Bitmap map  = this.map.getOriginal_image();
        int height = map.getHeight();
        int width = map.getWidth();
        int particleCounter = 0;
//        for(int x = 0; x < width;x++)
//        {
//
//
//            for(int y = 0; y < height;y++)
//            {
//                if((map.getPixel(x,y) << 8) == 0xFF0000FF)//FILLING THE MAP -> further steps split into rooms
//                {
//                    //double direction, Position position, Position last_position, double weight
//                    if(particleCounter == PARTICLES) {
//                        break;
//                    }
//                    this.particles[particleCounter++] = new Particle(0,new Position(x,y),initialweights);
//                }
//            }
//        }

        for(int i = 0; i < PARTICLES; i++)
        {
            Pixel pixel = new Pixel();
            int random = rand.nextInt(this.map.pixels_in_use.size() - 1);
            Log.d("random", Integer.toString(random));
            pixel = this.map.pixels_in_use.get(random);
            Log.d("XY pixel", Integer.toString(pixel.getX()) + " " + Integer.toString(pixel.getY()));
            Log.d("is used", Boolean.toString(pixel.isUsed()));
            while(pixel.isUsed() == true)
            {
                random = rand.nextInt(this.map.pixels_in_use.size() - 1);
                pixel = this.map.pixels_in_use.get(random);
            }

            this.map.pixels_in_use.get(random).setUsed(true);

            Position tmp = new Position(pixel.getX(), pixel.getY());
            this.particles[i] = new Particle(0, tmp, initialweights);
            this.map.getOriginal_image().setPixel(tmp.getX(), tmp.getY(), 0xFF00FF00);
        }




    }
}