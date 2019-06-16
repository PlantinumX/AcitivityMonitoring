package com.example.activitymonitoring;

public class Particle {
    private Position lastPos;
    private Position pos; //bitmap positions
    private double direction; // look into it
    private double weight;
    public int color;


    Particle(double direction, Position position, double weight)
    {
        this.direction = direction;
        this.pos = position;
        this.lastPos = new Position();
        this.weight = weight;
        this.color = 0xFFFFFF00;
    }

    public Particle(Particle particle) {
        this.pos = particle.pos;
        this.lastPos = particle.lastPos;
        this.direction = particle.direction;
        this.weight = particle.weight;
        this.color = particle.color;
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

    public double getWeight() {
        return weight;
    }

    public Position getLastPos() {
        return lastPos;
    }

    public void setLastPos(Position lastPos) {
        this.lastPos.x = lastPos.x;
        this.lastPos.y = lastPos.y;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
