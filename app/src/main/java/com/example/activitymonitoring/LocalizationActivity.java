package com.example.activitymonitoring;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class LocalizationActivity extends BaseActivity implements SensorEventListener
{

    private Classifier classifier;
    public SensorHandler sensorHandler;
    public SensorManager sensorManager;
    private float currentDegree = 0f;
    public ParticleFilter particleFilter;
    private Map map;
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
        sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    //TODO more sensitive
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {


            // get the angle around the z-axis rotated
            float degree = Math.round(event.values[0]);

            //if(event.values[0])

//            Log.d("50", Float.toString(degree));
            Button arrowImageView = findViewById(R.id.green_arrow);


            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation rotateAnimation = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            // how long the animation will take place
            rotateAnimation.setDuration(210);

            // set the animation after the end of the reservation status
            rotateAnimation.setFillAfter(true);

            // Start the animation

            arrowImageView.startAnimation(rotateAnimation);
            currentDegree = degree;
        }
        ImageView imageView = findViewById(R.id.image1);
        imageView.setImageBitmap(this.map.getOriginal_image());
        ParticleFilter.intersect(150.0f,400.0f,300.f,200.f,200.f,200.f,200.f,400.f);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    public void updateEditView(Record record)
    {

            //TAKE SMALLER WINDOW
            float[] result = classifier.predict(record);


    }
}
