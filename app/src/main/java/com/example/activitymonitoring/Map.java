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

    public Map(BaseActivity baseActivity)
    {
        this.original_image = BitmapFactory.decodeResource(baseActivity.getResources(), R.drawable.map_tug);
//        this.rooms = new ArrayList<>();
        this.positions[0] = new Position();//IF THERE IS TIME
        this.positions[1] = new Position();
        this.positions[2] = new Position();
        this.positions[3] = new Position();
        this.walls = new ArrayList<>();
//        initWalls();

    }

    private void initWalls()
    {
        //WALL 1 multiplication with 3 because scaling is different than taken from gimp
        Wall wall1 = new Wall(new Position(14 * 3,169 * 3), new Position(248 * 3,169 * 3),new Position(14 * 3,175 * 3),new Position(248 * 3,175 * 3));
        this.walls.add(wall1);

        //WALL 2 multiplication with 3 because scaling is different than taken from gimp
        Wall wall2 = new Wall(new Position(14 * 3,176 * 3), new Position(22 * 3,176 * 3),new Position(14 * 3,254 * 3),new Position(22 * 3,254 * 3));
        this.walls.add(wall2);

        //WALL 3 multiplication with 3 because scaling is different than taken from gimp
        Wall wall3 = new Wall(new Position(22 * 3,248 * 3), new Position(157 * 3,248 * 3),new Position(22 * 3,254* 3),new Position(157 * 3,254 * 3));
        this.walls.add(wall3);

        //WALL 4 multiplication with 3 because scaling is different than taken from gimp
        Wall wall4 = new Wall(new Position(138 * 3,189 * 3), new Position(157 * 3,248 * 3),new Position(22 * 3,254* 3),new Position(157 * 3,254 * 3));
        this.walls.add(wall4);

        //WALL 5 multiplication with 3 because scaling is different than taken from gimp
        Wall wall5 = new Wall(new Position(152 * 3,189 * 3), new Position(157 * 3,189 * 3),new Position(152 * 3,242 * 3),new Position(157 * 3,242 * 3));
        this.walls.add(wall5);

        //WALL 6 multiplication with 3 because scaling is different than taken from gimp
        Wall wall6 = new Wall(new Position(157 * 3,189 * 3), new Position(247 * 3,189 * 3),new Position(157 * 3,193 * 3),new Position(247 * 3,193 * 3));
        this.walls.add(wall6);

        //WALL 7 multiplication with 3 because scaling is different than taken from gimp
        Wall wall7 = new Wall(new Position(242 * 3,193 * 3), new Position(247 * 3,193 * 3),new Position(242 * 3,254 * 3),new Position(247 * 3,254 * 3));
        this.walls.add(wall7);

        //WALL 7 multiplication with 3 because scaling is different than taken from gimp
        Wall wall8 = new Wall(new Position(247 * 3,249 * 3), new Position(275 * 3,249 * 3),new Position(247 * 3,254 * 3),new Position(275 * 3,254 * 3));
        this.walls.add(wall8);

        //WALL 7 multiplication with 3 because scaling is different than taken from gimp
        Wall wall9 = new Wall(new Position(272 * 3,189 * 3), new Position(275 * 3,189 * 3),new Position(272 * 3,249 * 3),new Position(275 * 3,249 * 3));
        this.walls.add(wall9);

        //WALL 7 multiplication with 3 because scaling is different than taken from gimp
        Wall wall10 = new Wall(new Position(275 * 3,189 * 3), new Position(572 * 3,189 * 3),new Position(275 * 3,192 * 3),new Position(572 * 3,192 * 3));
        this.walls.add(wall10);

        //WALL 11 multiplication with 3 because scaling is different than taken from gimp
        Wall wall11 = new Wall(new Position(568 * 3,170 * 3), new Position(572 * 3,170 * 3),new Position(568 * 3,189 * 3),new Position(572 * 3,189 * 3));
        this.walls.add(wall11);

        //WALL 12 multiplication with 3 because scaling is different than taken from gimp
        Wall wall12 = new Wall(new Position(273 * 3,170 * 3), new Position(568 * 3,170 * 3),new Position(273 * 3,173 * 3),new Position(568 * 3,173 * 3));
        this.walls.add(wall12);

        //WALL 13 multiplication with 3 because scaling is different than taken from gimp
        Wall wall13 = new Wall(new Position(273 * 3,110 * 3), new Position(275 * 3,110 * 3),new Position(273 * 3,170 * 3),new Position(275 * 3,170 * 3));
        this.walls.add(wall13);

        //WALL 14 multiplication with 3 because scaling is different than taken from gimp
        Wall wall14 = new Wall(new Position(244 * 3,110 * 3), new Position(248 * 3,110 * 3),new Position(244 * 3,169 * 3),new Position(248 * 3,169 * 3));
        this.walls.add(wall14);

        //WALL 15 multiplication with 3 because scaling is different than taken from gimp
        Wall wall15 = new Wall(new Position(248 * 3,110 * 3), new Position(273 * 3,110 * 3),new Position(248 * 3,113 * 3),new Position(273 * 3,113 * 3));
        this.walls.add(wall15);
    }

    public void prepareMap() // MAKE 4 BITMAPS
    {
        this.original_image = this.original_image.copy(this.original_image.getConfig(),true);
        int height = this.original_image.getHeight();
        int width = this.original_image.getWidth();
//        int color = 0;
//        int[] colorValues = {0xFFFFFF00,0xFFFFFF00,0xFFFFFF00,0xFFFFFF00};
//        for(;color < 4;color++)
//        {
//            Position[] positions = new Position[4];
//            positions[0] = new Position();
//            positions[1] = new Position();
//            positions[2] = new Position();
//            positions[3] = new Position();
            for(int x = 0; x < width;x++)
            {
                for(int y = 0; y < height; y++)
                {
//                Log.d("MAP", " x " + Integer.toString(x) + " y " + Integer.toString(y)  + " Color : " + Integer.toHexString(original_image.getPixel(x,y) << 8));

                    if((original_image.getPixel(x,y) << 8) == 0xFFFFFF00) // shift right
                    {
//                        if(positions[0].getX() == 0 && positions[0].getY() == 0)
//                        {
//                            positions[0].setX(x);
//                            positions[1].setX(x);0xFF0000FF)
//                            positions[0].setY(y);
//                            positions[2].setY(y);
//                        }
//                        if(positions[1].getX() == x)
//                        {
//                            positions[1].setY(y);
//                        }
//                        if(positions[2].getY() == positions[0].getY())
//                        {
//                            positions[2].setX(x);
//                            positions[3].setX(x);
//                        }
//                        if(positions[2].getX() == x)
//                        {
//                            positions[3].setY(y);
//                        }
                        this.original_image.setPixel(x ,y , 0xFF0000FF);
//                    }

                    }else if((original_image.getPixel(x,y) << 8) == 0xFD050500) {
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

                    }

            }
//            Log.d("MAP","Sectors with Color Area" + colorValues[color]);
        }

        //We are going to divide into for rooms

        //this.rooms.add()
        Log.d("MAP","finished preparing map");
        for(Wall wall : this.walls) {
            height = wall.bottom_right.getY() - wall.top_left.getY();
            width = wall.bottom_right.getX() - wall.top_left.getX();
            for(int x = 0; x < width;x++)
            {
                for(int y = 0; y < height; y++)
                {
                    this.original_image.setPixel(wall.top_left.getX()+ x,wall.top_left.getY() + y,wall.color);

                }
            }
        }
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
