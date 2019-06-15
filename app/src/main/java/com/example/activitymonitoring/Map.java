package com.example.activitymonitoring;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
//HARDCODED VALUES FOR OUR ROOMS


public class Map
{
    private Bitmap original_image;
           
//    public List<Bitmap> rooms;
    public List<Wall> walls;
    Position[] positions = new Position[4]; // to get a smaller rectange for traversing through map
    int pixelMeterCoefficient;
    public Map(BaseActivity baseActivity)
    {
        this.original_image = BitmapFactory.decodeResource(baseActivity.getResources(), R.drawable.map_tug);
//        this.rooms = new ArrayList<>();
        this.positions[0] = new Position();//IF THERE IS TIME
        this.positions[1] = new Position();
        this.positions[2] = new Position();
        this.positions[3] = new Position();
        this.walls = new ArrayList<>();

    }


    public void prepareMap() // MAKE 4 BITMAPS
    {
        this.original_image = this.original_image.copy(this.original_image.getConfig(),true);
        int height = this.original_image.getHeight();
        int width = this.original_image.getWidth();

            for(int x = 0; x < width;x++)
            {
                for(int y = 0; y < height; y++)
                {
//                Log.d("MAP", " x " + Integer.toString(x) + " y " + Integer.toString(y)  + " Color : " + Integer.toHexString(original_image.getPixel(x,y) << 8));

                    if((original_image.getPixel(x,y) << 8) == 0xFFFFFF00) // shift right
                    {
                        this.original_image.setPixel(x ,y , 0xFF0000FF);

                    }
                    else if((original_image.getPixel(x,y) << 8) == 0xFD050500) {
                        int top_left_x = x;
                        int top_left_y = y;
                        int top_right_x = x;
                        int top_right_y = y;
                        //TODO CHECK IF POSITION IS ALREADY IN RECTANGLE
                        boolean isInBoundary = false;
                        for(Wall wall: this.walls)
                        {
                            if(wall.checkInBoundary(x,y)) {
                                isInBoundary = true;
                                break;
                            }
                        }
                        if(!isInBoundary) {
                            while((original_image.getPixel(top_right_x,y) << 8) == 0xFD050500) { //find top right x
                                top_right_x++;
                            }
                            top_right_x--;
                            int bottom_left_x = x;
                            int bottom_left_y = y; //missing
                            int bottom_right_x = top_right_x;
                            int bottom_right_y = y;
                            while((original_image.getPixel(x,bottom_left_y) << 8) == 0xFD050500 && (original_image.getPixel(top_right_x,bottom_right_y) << 8) == 0xFD050500) {
                                if(checkLinehasSameColor(x,top_right_x,bottom_left_y,0xFD050500)){
                                    bottom_left_y++;
                                    bottom_right_y++;
                                }
                                else {
                                    bottom_left_y--;
                                    bottom_right_y--;
                                    break;
                                }

                            }
                            this.walls.add(new Wall(new Position(top_left_x,top_left_y),new Position(top_right_x,top_right_y),new Position(bottom_left_x,bottom_left_y),new Position (bottom_right_x,bottom_right_y)));
                        }

                    }else if((original_image.getPixel(x,y) << 8) == 0x00ff0000) {
                            if(this.pixelMeterCoefficient == 0) {
                                int x_steps = x;
                                while((original_image.getPixel(x_steps,y) << 8) == 0x00ff0000) {
                                    x_steps++;
                                }
                                this.pixelMeterCoefficient = x_steps - x;
                            }

                        }

            }
        }

        //We are going to divide into for rooms

        //this.rooms.add()
        Log.d("MAP","finished preparing map");
        //TODO DEBUGGING PURPOSES
            //        for(Wall wall : this.walls) {
//            height = wall.bottom_right.getY() - wall.top_left.getY();
//            width = wall.bottom_right.getX() - wall.top_left.getX();
//            for(int x = 0; x < width;x++)
//            {
//                for(int y = 0; y < height; y++)
//                {
//                    this.original_image.setPixel(wall.top_left.getX()+ x,wall.top_left.getY() + y,wall.color);
//
//                }
//            }
//        }
        Log.d("MAP", "finished wallls");
    }

    public Bitmap getOriginal_image() {
        return original_image;
    }

    public void setOriginal_image(Bitmap original_image) {
        this.original_image = original_image;
    }

    private boolean checkLinehasSameColor(int top_left_x,int top_right_x,int y,int color) {
        for(int x = 0;x < top_right_x - top_left_x;x++) {
            if((original_image.getPixel(x+top_left_x,y) << 8) != color) {
                return false;
            }
        }
        return true;
    }
}
