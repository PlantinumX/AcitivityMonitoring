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
        double weight = 1.f / (double)PARTICLES; //first
        initParticlesIntoMap(weight);


        return 0;
    }

    double meterToPixelConverter(double distance) {
        return distance * this.map.pixelMeterCoefficient;

    }

    //TODO move particles gives me direction and distance
    //TODO move particles gives me direction and distance
    public void moveParticles(double distance, double direction) //mobile phone detected movement calculated distance we got stride + directioon
    {
        Log.d("PARTICLE FILTER ", "D: " + distance + " DIR: " + direction);
        double pixel_distance = meterToPixelConverter(distance);
        Log.d("PARTICLE FILTER ", "PD: " + pixel_distance);
        int id = 0;
        double tmpDirection = direction;
        for (Particle particle : particles) {
            Position position = particle.getPos();
            tmpDirection = Math.toRadians(direction) + 0.35 * new Random().nextDouble(); //some noise
//            Log.d("P","Particle " + id + " " + position.x + " " + position.y);

            particle.setLastPos(new Position(position));
            Position newPosition = new Position();
            newPosition.setY((int) (position.getY() + 0.15 * new Random().nextDouble() + 0.65f * pixel_distance * Math.sin(tmpDirection)));
            newPosition.setX((int) (position.getX() +  0.65 * new Random().nextDouble() + 0.95  * pixel_distance * Math.cos(tmpDirection)));
//            Log.d("P","NEW Particle " + newPosition.x + " " + newPosition.y);
            particle.setPos(newPosition);
            id++;

        }
        checkParticles();
        systematicVarianceResampling();
    }

    //OOM not today
    public boolean systematicVarianceResampling() //calculate new weights but how
    {
        Log.d("MAP","SYSTEMATIC VARIANCE RESAMPLING");
        Particle[] resampled_particles = new Particle[PARTICLES];
        // compute particle cdf
        double[] cum = new double[PARTICLES];
        cum[0] = 0.0;
        //generate pdf
        for (int i = 1; i < PARTICLES; i++) {
            cum[i] = cum[i - 1] + particles[i].getWeight();
        }

        Random rng = new Random();
        double p_step = 1.0 / PARTICLES; // probability step size for resampling (new sample weight)
        double p_resample = (rng.nextDouble() - 1) * p_step;
        int cum_index = 0;

        for (int i = 0; i < PARTICLES; i++) {
            p_resample += p_step; // that is threshold and is incremented in each iteration

            //skip until next threshold is reached
            while (cum_index < (PARTICLES - 1) && (Double.compare(p_resample ,cum[cum_index] ) > 0 || Double.compare(particles[cum_index].getWeight() ,0.0f) == 0)) {
                cum_index++;
            }

                //insert into new particle array
                if (Double.compare(particles[cum_index].getWeight(),0.0) == 0)
                    resampled_particles[i] = new Particle(resampled_particles[i - 1]);
                else { //insert into new particle array
                    resampled_particles[i] = new Particle(particles[cum_index]);
                }
            //set old weight  1/N
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

//            Log.d("PARTICLE ", "PARTICLE ID "+id+ " " + position.getX() + " " + position.getY());
//            Log.d("PARTICLE ", "LAST POSITION "+ " " + lastPosition.getX() + " " + lastPosition.getY());


                for(Wall wall : this.map.walls) {
//                Log.d("P", "TOP LEFT "+wall.top_left.x + " " + wall.top_left.y);
//                Log.d("P", "BOOTOM RIGHT "+wall.bottom_right.x + " " + wall.bottom_right.y);
                    Position intersectionWithTopBorder = intersect(lastPosition,position,wall.top_left,wall.top_right);

                    Position intersectionWithBottomBorder = intersect(lastPosition,position,wall.bottom_left,wall.bottom_right);
                    Position intersectionWithRightBorder = intersect(lastPosition,position,wall.top_right,wall.bottom_right);

                    Position intersectionWithLeftBorder = intersect(lastPosition,position,wall.top_left,wall.top_right);
                    if(intersectionWithTopBorder != null || intersectionWithBottomBorder != null || intersectionWithRightBorder != null || intersectionWithLeftBorder != null || particle.getPos().x > 1500 || particle.getPos().x < 0 || particle.getPos().y > 900|| particle.getPos().y < 250 || this.map.getOriginal_image().getPixel((int)particle.getPos().x,(int)particle.getPos().y ) != 0xFFFFFFFF)
                    {
                        Log.d("PARTICLE FILTEr", "COLLISION DETECTED\n");
                        particle.setWeight(0.f);
                        isCollided = true;
                        break;
                    }

                }
                if(!isCollided) {
//                Log.d("index not collidet", Integer.toString(cnt));
                    particleSize++;
                }

        }
        Log.d("PARTICLE FILTER ","particle size " + particleSize);


        //IF THERE ARE LESS THAN 20 PARTICLES LEFT LOCALIZATION IS NOT ACCURATE SO START FROM SCRATCH AGAIN
        if(particleSize < 20)
        {
            init();
        }


        double sumweight = 0.f;
        for(Particle particle : particles)
        {
            sumweight += particle.getWeight();
        }
        for(Particle particle : particles)
        {
            if (Double.compare(particle.getWeight(), 0.f) > 0) {
                particle.setWeight(particle.getWeight()/(double)sumweight);
            }
        }


    }

//https://stackoverflow.com/questions/15514906/how-to-check-intersection-between-a-line-and-a-rectangle
    //TODO CHECK IF IT WORKS
    public  static Position intersect(Position start_1,Position end_1,Position start_2,Position end_2) {

        Position result = null;

        double
                s1_x = end_1.x - start_1.x,
                s1_y = end_1.y - start_1.y,

                s2_x = end_2.x - start_2.x,
                s2_y = end_2.y - start_2.y,

                s = (-s1_y * (start_1.x - start_2.x) + s1_x * ( start_1.y - start_2.y)) / (-s2_x * s1_y + s1_x * s2_y),
                t = ( s2_x * (start_1.y - start_2.y) - s2_y * ( start_1.x - start_2.x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
        {
            result = new Position(
                    (int) (start_1.x + (t * s1_x)),
                    (int) (start_1.y + (t * s1_y)));
        }

        return result;
    }

    void initParticlesIntoMap(double initialweights) {
//        Log.d("P","INIT PARTICLES");
        Random rand = new Random();

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
//            this.map.getOriginal_image().setPixel((int)tmp.getX(), (int)tmp.getY(), 0xFF00FF00);
//            Log.d("P","PAINTED MAP At " + tmp.x + " " +tmp.y);

        }

    }
}