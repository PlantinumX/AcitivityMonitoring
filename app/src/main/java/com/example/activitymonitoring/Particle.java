package com.example.activitymonitoring;

public class Particle {
    private Position last_pos;
    private Position pos;
    private double direction; // look into it
    private double likelihood;
    private double weight;


    Particle(double likelihood)
    {

    }

    Particle(double likelihood, double direction, Position position, Position last_position, double weight)
    {
        this.likelihood = likelihood;
        this.direction = direction;
        this.pos = new Position(position);
        this.last_pos = new Position(last_position);
        this.weight = weight;
    }

    public Position getLast_pos()
    {
        return last_pos;
    }

    public void setLast_pos(Position last_pos)
    {
        this.last_pos = last_pos;
    }

    public Position getPos()
    {
        return pos;
    }

    public void setPos(Position pos)
    {
        this.pos = pos;
    }

    public double getDirection()
    {
        return direction;
    }

    public void setDirection(double direction)
    {
        this.direction = direction;
    }

    public double getLikelihood()
    {
        return likelihood;
    }

    public void setLikelihood(double likelihood)
    {
        this.likelihood = likelihood;
    }
}
