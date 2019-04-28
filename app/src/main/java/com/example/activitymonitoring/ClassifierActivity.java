package com.example.activitymonitoring;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ClassifierActivity  extends AppCompatActivity
{

	private Classifier classifier;
	public SensorHandler sensorHandler;
	public SensorManager sensorManager;
	private static List<Float> accelerometer_x = new ArrayList<>();
	private static List<Float> accelerometer_y = new ArrayList<>();
	private static List<Float> input_signal =  new ArrayList<>();
	private static List<Float> accelerometer_z = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_main);
		classifier = new Classifier(getApplicationContext());

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorHandler = new SensorHandler(this);
		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) , SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) , SensorManager.SENSOR_DELAY_UI);
	}


	public void updateEditView(Accelerometer accelerometer) {
		TextView downstairsProb = findViewById(R.id.Downstairs_prob);
		TextView joggingProb = findViewById(R.id.Jogging_prob);
		TextView SittingProb = findViewById(R.id.Sitting_prob);
		TextView StandingProb = findViewById(R.id.Standing_prob);
		TextView UpstairsProb = findViewById(R.id.Upstairs_prob);
		TextView WalkingProb = findViewById(R.id.Walking_prob);

		accelerometer_x.add(accelerometer.x);
		accelerometer_y.add(accelerometer.y);
		accelerometer_z.add(accelerometer.z);

		//TAKE SMALLER WINDOW
		if (accelerometer_x.size() == 90 && accelerometer_y.size() == 90 && accelerometer_z.size() == 90) {
			input_signal.addAll(accelerometer_x);
			input_signal.addAll(accelerometer_y);
			input_signal.addAll(accelerometer_z);
			float[] result = classifier.predictProbabilities(SensorHandler.convertToFloatArray(input_signal));
			downstairsProb.setText(Float.toString(result[0]));
			joggingProb.setText(Float.toString(result[1]));
			SittingProb.setText(Float.toString(result[2]));
			StandingProb.setText(Float.toString(result[3]));
			UpstairsProb.setText(Float.toString(result[4]));
			WalkingProb.setText(Float.toString(result[5]));
			input_signal.clear();
			accelerometer_x.clear();
			accelerometer_z.clear();
			accelerometer_y.clear();
		}

	}



}
