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

public class ClassifierActivity  extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
	private boolean isDataContent;
	private Classifier classifier;
	public SensorHandler sensorHandler;
	public SensorManager sensorManager;
	private String selectedActivity;
	private static List<Float> accelerometer_x = new ArrayList<>();
	private static List<Float> accelerometer_y = new ArrayList<>();
	private static List<Float> input_signal =  new ArrayList<>();
	private static List<Float> accelerometer_z = new ArrayList<>();

	public void onItemSelected(AdapterView<?> parent, View view,
	                           int pos, long id) {
		// An item was selected. You can retrieve the selected item using
		// parent.getItemAtPosition(pos)
		selectedActivity = parent.getItemAtPosition(pos).toString();
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		isDataContent = intent.getBooleanExtra(MainActivity.GENERATE_DATA,false);
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
			classifier = new Classifier(getApplicationContext());
		}

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorHandler = new SensorHandler(this);

		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) , SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) , SensorManager.SENSOR_DELAY_UI);
	}


	public void updateEditView(Accelerometer accelerometer) {
		if (isDataContent)
		{
			TextView xAxis = findViewById(R.id.X_Axis);
			TextView yAxis = findViewById(R.id.Y_Axis);
			TextView zAxis = findViewById(R.id.Z_Axis);
			sensorHandler.writeToActivityLogFile(selectedActivity, accelerometer);
			xAxis.setText(Float.toString(accelerometer.x));
			yAxis.setText(Float.toString(accelerometer.y));
			zAxis.setText(Float.toString(accelerometer.z));
		}
		else
		{
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
			if (accelerometer_x.size() == SensorHandler.WINDOW_SIZE && accelerometer_y.size() == SensorHandler.WINDOW_SIZE && accelerometer_z.size() == SensorHandler.WINDOW_SIZE) {

				input_signal.addAll(accelerometer_x);
				input_signal.addAll(accelerometer_y);
				input_signal.addAll(accelerometer_z);
				float[] data = SensorHandler.convertToFloatArray(input_signal);
				input_signal.clear();
				accelerometer_x.clear();
				accelerometer_z.clear();
				accelerometer_y.clear();
				float[] means = SensorHandler.calculateMeans(data,SensorHandler.WINDOW_SIZE);
				float[] variances = SensorHandler.calculateVariances(data,SensorHandler.WINDOW_SIZE,means);
				data = SensorHandler.normalizeData(data,SensorHandler.WINDOW_SIZE,means,variances);
				float[] result = classifier.predictProbabilities(data);
				sensorHandler.writeToActivityLogFile(result);
				downstairsProb.setText(Float.toString(result[0]));
				joggingProb.setText(Float.toString(result[1]));
				SittingProb.setText(Float.toString(result[2]));
				StandingProb.setText(Float.toString(result[3]));
				UpstairsProb.setText(Float.toString(result[4]));
				WalkingProb.setText(Float.toString(result[5]));

			}
		}
	}
}
