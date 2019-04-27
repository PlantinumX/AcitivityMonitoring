package com.example.activitymonitoring;

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
		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) , SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) , SensorManager.SENSOR_DELAY_FASTEST);
	}


	private int prediction()
	{

		int result = 0;
		double[] data = new double[600];

		for(int i=0; i<200; i+=3)
		{
			data[i] = accelerometer_x.get(i);
			data[i+1] = accelerometer_y.get(i);
			data[i+2] = accelerometer_z.get(i);
		}

//		result = classifier.predictProbabilities(data);

		accelerometer_x.clear();
		accelerometer_y.clear();
		accelerometer_z.clear();

		return result;

	}


	public void updateEditView(Accelerometer accelerometer)
	{
		TextView downstairsProb = findViewById(R.id.Downstairs_prob);
		TextView joggingProb = findViewById(R.id.Jogging_prob);
		TextView SittingProb = findViewById(R.id.Sitting_prob);
		TextView StandingProb = findViewById(R.id.Standing_prob);
		TextView UpstairsProb = findViewById(R.id.Upstairs_prob);
		TextView WalkingProb = findViewById(R.id.Walking_prob);
//		TextView activity = findViewById(R.id.Activity);
//

		accelerometer_x.add(accelerometer.x);
		accelerometer_y.add(accelerometer.y);
		accelerometer_z.add(accelerometer.z);

//		if (accelerometer_x.size() == 200 && accelerometer_y.size() == 200 && accelerometer_z.size() == 200)
//		{
//			int result = prediction();
//
//			switch (result)
//			{
//				case 0:
//					activity.setText("Walking");
//					break;
//				case 1:
//					activity.setText("Jogging");
//					break;
//				case 2:
//					activity.setText("Sitting");
//					break;
//				case 3:
//					activity.setText("Standing");
//					break;
//				case 4:
//					activity.setText("Upstairs");
//					break;
//				case 5:
//					activity.setText("Downstairs");
//					break;
//			}
			if(accelerometer_x.size() == 200 && accelerometer_y.size()  == 200 && accelerometer_z.size() == 200)
			{
				input_signal.addAll(accelerometer_x);
				input_signal.addAll(accelerometer_y);
				input_signal.addAll(accelerometer_z);
				float[] result = classifier.predictProbabilities(toFloatArray(input_signal));
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
//



//		}
	}
	private float[] toFloatArray(List<Float> list)
	{
		int i = 0;
		float[] array = new float[list.size()];

		for (Float f : list) {
			array[i++] = (f != null ? f : Float.NaN);
		}
		return array;
	}


}
