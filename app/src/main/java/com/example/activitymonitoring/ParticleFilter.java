package com.example.activitymonitoring;


import android.graphics.Bitmap;
import android.util.Log;

import java.util.Random;

public class ParticleFilter {
    private final int PARTICLES = 15000; //AMOUNT OF PARTICLES
    public Particle[] particles;
    public Map map;

    ParticleFilter(Map map) {
        particles = new Particle[PARTICLES];
        this.map = map;
        init();
    }


    public int init() {
        double weight = 1 / PARTICLES; //first
        initParticlesIntoMap(weight);


        return 0;
    }

    double meterToPixelConverter(double distance) {
        return distance * this.map.pixelMeterCoefficient;

    }

    //TODO move particles gives me direction and distance
    public void moveParticles(double distance, double direction) //mobile phone detected movement calculated distance we got stride + directioon
    {

        double pixel_distance = meterToPixelConverter(distance);
        direction = Math.toRadians(direction);
        for (Particle particle : particles) {
            Position position = particle.getPos();
            particle.setLastPos(new Position(position));
            Position newPosition = new Position();
            newPosition.setX((int) (position.getX() + pixel_distance * Math.cos(direction)));//TODO WE MUST DO SOMETHIG ABOUT NOISE
            newPosition.setY((int) (position.getX() + pixel_distance * Math.sin(direction)));//TODO WE MUST DO SOMETHING ABOUT NOISE
            particle.setPos(newPosition);
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
        // compute particle cdf
        double[] cdf = new double[PARTICLES];
        cdf[0] = 0.0;
        for (int i = 1; i < PARTICLES; i++) {
            cdf[i] = cdf[i - 1] + particles[i].getWeight();
        }

        Random rng = new Random();
        double p_step = 1.0 / PARTICLES; // probability step size for resampling (new sample weight)
        double p_resample = (rng.nextDouble() - 1) * p_step;
        int cdf_idx = 0;

        for (int i = 1; i < PARTICLES; i++) {
            p_resample += p_step;

            while (cdf_idx < (PARTICLES - 1) && (p_resample > cdf[cdf_idx] || particles[cdf_idx].getWeight() == 0.0)) {
                cdf_idx++;
            }

            // if the resample particle weight is 0.0 (should only occur for the last part of the
            // cdf) then we take a
            // particle with non-zero weight..
            if (particles[cdf_idx].getWeight() == 0.0)
                resampled_particles[i] = new Particle(resampled_particles[i - 1]);
            else
                resampled_particles[i] = new Particle(particles[cdf_idx]);

            resampled_particles[i].setWeight(p_step);
        }

        particles = resampled_particles;
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
        Log.d("PARTICLE FILTER ","particle size " + particleSize);

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
        Random rand = new Random();
        Bitmap map  = this.map.getOriginal_image();
        int height = map.getHeight();
        int width = map.getWidth();


        for(int i = 0; i < PARTICLES; i++)
        {
            Pixel pixel = new Pixel();
            int random = rand.nextInt(this.map.pixels_in_use.size() - 1);
//            Log.d("random", Integer.toString(random));
            pixel = this.map.pixels_in_use.get(random);
//            Log.d("XY pixel", Integer.toString(pixel.getX()) + " " + Integer.toString(pixel.getY()));
//            Log.d("is used", Boolean.toString(pixel.isUsed()));
            while(pixel.isUsed() == true)
            {
                random = rand.nextInt(this.map.pixels_in_use.size() - 1);
                pixel = this.map.pixels_in_use.get(random);
            }

            this.map.pixels_in_use.get(random).setUsed(true);

            Position tmp = new Position(pixel.getX(), pixel.getY());
            this.particles[i] = new Particle(0, tmp, initialweights);
            this.map.getOriginal_image().setPixel((int)tmp.getX(), (int)tmp.getY(), 0xFF00FF00);
        }




    }
}