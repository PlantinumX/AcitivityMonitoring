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
        int height = this.image.getHeight();
        int width = this.image.getWidth();
        for(int x = 0; x < height;x++)
        {
            for(int y = 0; y < width; y++)
            {
                Log.d("MAP", "Color : " + image.getPixel(x,y));

                if(image.getPixel(x,y) == 0xFFFFFF)
                {
                    this.image.setPixel(x ,y , 0x0000FF);
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
