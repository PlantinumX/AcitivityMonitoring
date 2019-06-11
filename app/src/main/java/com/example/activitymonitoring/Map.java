package com.example.activitymonitoring;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Map
{
    private Bitmap image;
    public Map()
    {
        this.image = BitmapFactory.decodeResource(getR, R.drawable.map_tug);
    }
}
