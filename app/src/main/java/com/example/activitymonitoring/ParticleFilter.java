package com.example.activitymonitoring;


import android.graphics.Bitmap;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;

import java.util.Random;

public class ParticleFilter
{
    private final int PARTICLES = 7500; //AMOUNT OF PARTICLES
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

    double meterToPixelConverter(double distance) {
        return distance * this.map.pixelMeterCoefficient;

    }

    //TODO move particles gives me direction and distance
    public void moveParticles(double distance,double direction) //mobile phone detected movement calculated distance we got stride + directioon
    {
        double pixel_distance = meterToPixelConverter(distance);
        direction = Math.toRadians(direction);
        for(Particle particle : particles)
        {
            Position position = particle.getPos();
            particle.setLastPos(position);
            Position newPosition = new Position();
            newPosition.setX((int) (position.getX() + pixel_distance * Math.cos(direction)));//TODO WE MUST DO SOMETHIG ABOUT NOISE
            newPosition.setY((int) (position.getX() + pixel_distance * Math.sin(direction)));//TODO WE MUST DO SOMETHING ABOUT NOISE
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
    public void checkParticles()
    {
        int particleSize = 0;

        for(Particle particle : particles)
        {
            Position position = particle.getPos();
            Position lastPosition = particle.getLastPos();
            boolean isCollided = false;
            for(Wall wall : this.map.walls) {
                //loat px1, float py1, float px2, float py2

                Position intersectionWithTopBorder = intersect(lastPosition,position,wall.top_left,wall.top_right);

                Position intersectionWithBottomBorder = intersect(lastPosition,position,wall.bottom_left,wall.bottom_right);
                Position intersectionWithRightBorder = intersect(lastPosition,position,wall.top_right,wall.bottom_right);

                Position intersectionWithLeftBorder = intersect(lastPosition,position,wall.top_left,wall.top_right);
                if(intersectionWithTopBorder != null || intersectionWithBottomBorder != null || intersectionWithRightBorder != null || intersectionWithLeftBorder != null) {
                    Log.d("PARTICLE FILTEr", "COLLISION DETECTED\n");
                    particle.setWeight(0.f);
                    isCollided = true;
                    break;
                }

            }
            if(!isCollided) {
                particleSize++;
            }

        }
        for(Particle particle : particles)
        {
            if (Double.compare(particle.getWeight(), 0.f) != 0) {
                continue;
            }
            particle.setWeight(1/particleSize);


        }

    }

//https://stackoverflow.com/questions/15514906/how-to-check-intersection-between-a-line-and-a-rectangle
    //TODO CHECK IF IT WORKS
    public  static Position intersect(Position start_1,Position end_1,Position start_2,Position end_2) {

        Position result = null;

        double
                s1_x = end_1.x - start_1.x /*pLine1.x2 - pLine1.x1*/,
                s1_y = end_1.y - start_1.y /*pLine1.y2 - pLine1.y1*/,

                s2_x = end_2.x - start_2.x /*pLine2.x2 - pLine2.x1*/,
                s2_y = end_2.y - start_2.y /*pLine2.y2 - pLine2.y1*/,

                s = (-s1_y * (start_1.x - start_2.x /*pLine1.x1 - pLine2.x1*/) + s1_x * ( start_1.y - start_2.y /*pLine1.y1 - pLine2.y1*/)) / (-s2_x * s1_y + s1_x * s2_y),
                t = ( s2_x * (start_1.y - start_2.y /*pLine1.y1 - pLine2.y1*/) - s2_y * ( start_1.x - start_2.x /*pLine1.x1 - pLine2.x1*/)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
        {
            // Collision detected
            result = new Position(
                    (int) (start_1.x /*pLine1.x1 */+ (t * s1_x)),
                    (int) (start_1.y/*pLine1.y1 */+ (t * s1_y)));
        }   // end if

        return result;
    }

    void initParticlesIntoMap(double initialweights) {
        Random xAxis = new Random();
        Random yAxis = new Random();
        Bitmap map  = this.map.getOriginal_image();
        int height = map.getHeight();
        int width = map.getWidth();
        int particleCounter = 0;
        for(int x = 0; x < width;x++)
        {
            for(int y = 0; y < height;y++)
            {
                if((map.getPixel(x,y) << 8) == 0xFF0000FF)//FILLING THE MAP -> further steps split into rooms
                {
                    //double direction, Position position, Position last_position, double weight
                    if(particleCounter == PARTICLES) {
                        break;
                    }
                    this.particles[particleCounter++] = new Particle(0,new Position(x,y),initialweights);
                }
            }
        }

    }
}