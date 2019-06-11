package com.example.activitymonitoring;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.ColorInt;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Map
{
    private Bitmap image;
    private List<Rectangle> rooms;

    public Map(BaseActivity baseActivity)
    {
        this.image = BitmapFactory.decodeResource(baseActivity.getResources(), R.drawable.map_tug);
        this.rooms = new ArrayList<>();

    }

    public void prepareMap()
    {
        this.image = this.image.copy(this.image.getConfig(),true);
        int height = this.image.getHeight();
        int width = this.image.getWidth();
        for(int x = 0; x < width;x++)
        {
            for(int y = 0; y < height; y++)
            {
//                Log.d("MAP", " x " + Integer.toString(x) + " y " + Integer.toString(y)  + " Color : " + Integer.toHexString(image.getPixel(x,y) << 8));

                if((image.getPixel(x,y) << 8) == 0xFFFFFF00) // shift right
                {
                    this.image.setPixel(x ,y , 0xFF0000FF);
                }
            }
        }
        Log.d("MAP","finished preparing map");
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
