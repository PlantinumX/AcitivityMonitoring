package com.example.activitymonitoring;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ClassifierActivity extends BaseActivity implements AdapterView.OnItemSelectedListener
{
	private boolean isDataContent;
	private Classifier classifier;
	public SensorHandler sensorHandler;
	public SensorManager sensorManager;
	private String selectedActivity;
	private static List<Float> input_signal = new ArrayList<>();

	public void onItemSelected(AdapterView<?> parent, View view,
	                           int pos, long id) {
		// An item was selected. You can retrieve the selected item using
		// parent.getItemAtPosition(pos)
		selectedActivity = parent.getItemAtPosition(pos).toString();
	}

	public void onNothingSelected(AdapterView<?> parent)
	{
		// Another interface callback
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		isDataContent = intent.getBooleanExtra(MainActivity.GENERATE_DATA, false);
		if (isDataContent)
		{
			setContentView(R.layout.content_data_main);
			Spinner spinner = (Spinner) findViewById(R.id.acitivity_spinner);
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.activity_array, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(this);
		}
		else
		{
			setContentView(R.layout.content_main);
			sensorHandler = new SensorHandler(this);
			try
			{

				classifier = new Classifier(this);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
	}


	public void updateEditView(Record record)
	{
		if (isDataContent)
		{
			TextView xAxis = findViewById(R.id.X_Axis);
			TextView yAxis = findViewById(R.id.Y_Axis);
			TextView zAxis = findViewById(R.id.Z_Axis);
//			sensorHandler.writeToActivityLogFile(selectedActivity, accelerometer);
//			xAxis.setText(Float.toString(accelerometer.x));
//			yAxis.setText(Float.toString(accelerometer.y));
//			zAxis.setText(Float.toString(accelerometer.z));
		}
		else
		{
			TextView SittingProb = findViewById(R.id.Sitting_prob);
			TextView StandingProb = findViewById(R.id.Standing_prob);
			TextView WalkingProb = findViewById(R.id.Walking_prob);

			//TAKE SMALLER WINDOW
			float[] result = classifier.predict(record);
			WalkingProb.setText(Float.toString(result[0]));
			StandingProb.setText(Float.toString(result[2]));
			SittingProb.setText(Float.toString(result[1]));

		}
	}
}
