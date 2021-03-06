package com.example.activitymonitoring;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//HARDCODED VALUES FOR OUR ROOMS


public class Map {
    private Bitmap original_image;

    //    public List<Bitmap> rooms;
    public List<Wall> walls;
    double pixelMeterCoefficient;
    public ArrayList<Pixel> pixels_in_use;
    public Position estimated_pos;

    public Map(BaseActivity baseActivity) {
        this.estimated_pos = new Position();
        this.original_image = BitmapFactory.decodeResource(baseActivity.getResources(), R.drawable.map_tug);
        this.walls = new ArrayList<>();
        this.pixels_in_use = new ArrayList<>();

        this.pixelMeterCoefficient = 0;
    }


    public void prepareMap() // MAKE 4 BITMAPS
    {
        this.original_image = this.original_image.copy(this.original_image.getConfig(), true);
        int height = this.original_image.getHeight();
        int width = this.original_image.getWidth();
//        Log.d("MAP", "height " + height + " width " + width);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
//                Log.d("MAP", " x " + Integer.toString(x) + " y " + Integer.toString(y)  + " Color : " + Integer.toHexString(original_image.getPixel(x,y) << 8));

                if ((original_image.getPixel(x, y) << 8) == 0xFFFFFF00) // shift right
                {
                    //this.original_image.setPixel(x ,y , 0xFF0000FF);
                    pixels_in_use.add(new Pixel(x, y, false));
                } else if ((original_image.getPixel(x, y) << 8) == 0xFD050500) {
                    int top_left_x = x;
                    int top_left_y = y;
                    int top_right_x = x;
                    int top_right_y = y;
                    //TODO CHECK IF POSITION IS ALREADY IN RECTANGLE
                    boolean isInBoundary = false;
                    for (Wall wall : this.walls) {
                        if (wall.checkInBoundary(x, y)) {
                            isInBoundary = true;
                            break;
                        }
                    }
                    if (!isInBoundary) {
                        while ((original_image.getPixel(top_right_x, y) << 8) == 0xFD050500) { //find top right x
                            top_right_x++;
                        }
                        top_right_x--;
                        int bottom_left_x = x;
                        int bottom_left_y = y; //missing
                        int bottom_right_x = top_right_x;
                        int bottom_right_y = y;
                        while ((original_image.getPixel(x, bottom_left_y) << 8) == 0xFD050500 && (original_image.getPixel(top_right_x, bottom_right_y) << 8) == 0xFD050500) {
                            if (checkLinehasSameColor(x, top_right_x, bottom_left_y, 0xFD050500)) {
                                bottom_left_y++;
                                bottom_right_y++;
                            } else {
                                bottom_left_y--;
                                bottom_right_y--;
                                break;
                            }

                        }
                        this.walls.add(new Wall(new Position(top_left_x, top_left_y), new Position(top_right_x, top_right_y), new Position(bottom_left_x, bottom_left_y), new Position(bottom_right_x, bottom_right_y)));
                    }

                } else if ((original_image.getPixel(x, y) << 8) == 0x00ff0000) {
                    if (this.pixelMeterCoefficient == 0) {
                        int x_steps = x;
                        while ((original_image.getPixel(x_steps, y) << 8) == 0x00ff0000) {
                            x_steps++;
                        }
                        this.pixelMeterCoefficient = (x_steps - x) / (double) 5;
                    }

                }

            }
        }

        //We are going to divide into for rooms

        //this.rooms.add()
//        Log.d("MAP", "finished preparing map");
        //TODO DEBUGGING PURPOSES
//        for(Wall wall : this.walls) {
//            height = (int)(wall.bottom_right.getY() - wall.top_left.getY());
//            width = (int )(wall.bottom_right.getX() - wall.top_left.getX());
//            Log.d("MAP", "TOP LEFT "+wall.top_left.x + " " + wall.top_left.y);
//            Log.d("MAP", "BOOTOM RIGHT "+wall.bottom_right.x + " " + wall.bottom_right.y);
//            for(int x = 0; x < width;x++)
//            {
//                for(int y = 0; y < height; y++)
//                {
//                    this.original_image.setPixel((int)wall.top_left.getX()+ x,(int)wall.top_left.getY() + y,wall.color);
//
//                }
//            }
//            Log.d("MAP", "COLLOR " + Integer.toHexString(wall.color));
//        }
//        Log.d("MAP", "finished wallls");
    }

    public Bitmap getOriginal_image() {
        return original_image;
    }

    public void setOriginal_image(Bitmap original_image) {
        this.original_image = original_image;
    }

    private boolean checkLinehasSameColor(int top_left_x, int top_right_x, int y, int color) {
        for (int x = 0; x < top_right_x - top_left_x; x++) {
            if ((original_image.getPixel(x + top_left_x, y) << 8) != color) {
                return false;
            }
        }
        return true;
    }

    public void draw_estimated_Position(Particle particles[], Bitmap map) {
        ArrayList<Double> x = new ArrayList<Double>();
        ArrayList<Double> y = new ArrayList<Double>();


        for (int i = 0; i < particles.length; i++) {
            x.add(particles[i].getPos().x);
            y.add(particles[i].getPos().y);

        }

        Collections.sort(x);
        Collections.sort(y);

        estimated_pos.x = x.get(x.size() / 2);
        estimated_pos.y = y.get(y.size() / 2);


        for (int vert = -10; vert < 10; vert++) {
            for (int hor = -10; hor < 10; hor++) {
                map.setPixel((int) estimated_pos.x + hor, (int) estimated_pos.y + vert, 0xFFFF0000);
            }
        }

    }

    public void delete_estimated_postion(Bitmap map) {
        for (int vert = -10; vert < 10; vert++) {
            for (int hor = -10; hor < 10; hor++) {
                map.setPixel((int) estimated_pos.x + hor, (int) estimated_pos.y + vert, 0xFFFFFFFF);
            }
        }
    }

}
