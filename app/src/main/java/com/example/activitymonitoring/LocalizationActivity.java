package com.example.activitymonitoring;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class LocalizationActivity extends BaseActivity
{
    private boolean isDataContent;
    private Classifier classifier;
    public SensorHandler sensorHandler;
    public SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.content_localization_main);
        sensorHandler = new SensorHandler(this);
        try {

            classifier = new Classifier(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        RotateAnimation rotateAnimation = new RotateAnimation(180.0f, 0.0f,  Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setDuration(10000);
        rotateAnimation.setFillAfter(true);
        Button arrowImageView = findViewById(R.id.green_arrow);
        arrowImageView.startAnimation(rotateAnimation);
    }

    public void updateEditView(Record record)
    {

            //TAKE SMALLER WINDOW
            float[] result = classifier.predict(record);

    }
}
