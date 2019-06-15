package com.example.activitymonitoring;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LocalizationActivity extends BaseActivity
{

    private Classifier classifier;
    public SensorHandler sensorHandler;
    public SensorManager sensorManager;
    public ParticleFilter particleFilter;
    private Map map;
    private Motion motion = new Motion();
    private double mean_orientation = 0;
    private double duration_sec;
    private int step_cnt = 0;
    private double distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.content_localization_main);
        sensorHandler = new SensorHandler(this);
        try {

            classifier = new Classifier(this);
            this.map = new Map(this);
            this.map.prepareMap();
            this.particleFilter =  new ParticleFilter(this.map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void updateEditView(Record record)
    {
        //TAKE SMALLER WINDOW

        motion.sample_cnt++;

        float[] result = classifier.predict(record);

        motion.angle.add(record.orientation);

        //sitting = [1]
        //walking = [0]
        //standing = [2]
        if(result[0] > result[1] && result[0] > result[2])
        {
            motion.duration += record.duration;
        }


        if(motion.sample_cnt == 30)
        {

            for(int i = 0; i < motion.angle.size(); i++)
            {
                mean_orientation += motion.angle.get(i);
            }

            mean_orientation /= motion.angle.size();

//            Log.d("cycle", Long.toString(time_for_cyrcle));
//            Log.d("duration: ", Long.toString(motion.duration));
//            Toast.makeText(this, "duration: " + Long.toString(motion.duration) + "mean angle: " + Double.toString(orientation), Toast.LENGTH_LONG);

            duration_sec = (double)motion.duration/ 1000;

            step_cnt = (int)((duration_sec * 2) + 0.5);
            distance = step_cnt * 0.65;
//            Log.d("activity 0 ",Double.toString(result[0]));
//            Log.d("activity 1 ",Double.toString(result[1]));
//            Log.d("activity 2 ",Double.toString(result[2]));
//            Log.d("duration_sec", Double.toString(duration_sec));
//            Log.d("distance", Double.toString(distance));
//            Log.d("steps", Integer.toString(step_cnt));
//
//            Log.d("mean angle", Double.toString(mean_orientation));
//            Log.d("motion angle size", Double.toString(motion.angle.size()));
//            Toast.makeText(this, "duration: " + distance + "mean angle: " + mean_orientation , Toast.LENGTH_LONG).show();
            if(Double.compare(distance,0.f) != 0) {

                particleFilter.moveParticles(distance,mean_orientation);
            }

            motion.duration = (long)0;
            mean_orientation = 0;
            motion.sample_cnt = 0;
            motion.angle.clear();
        }
        ImageView imageView = findViewById(R.id.image1);
        imageView.setImageBitmap(this.map.getOriginal_image());


    }
}
