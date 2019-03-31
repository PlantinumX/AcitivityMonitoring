package com.example.activitymonitoring;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity  {

	public SensorHandler sensorHandler;
	public SensorManager sensorManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorHandler = new SensorHandler(this);
		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) , SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) , SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL));
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
	}
	public void updateEditView(Accelerometer accelerometer,Gyroscope gyroscope) {
		TextView xAxis = findViewById(R.id.x_Axis);
		TextView yAxis = findViewById(R.id.y_Axis);
		TextView zAxis = findViewById(R.id.z_Axis);
		TextView xRot = findViewById(R.id.x_Rot);
		TextView yRot = findViewById(R.id.y_Rot);
		TextView zRot = findViewById(R.id.z_Rot);

		xAxis.setText(Double.toString(accelerometer.x));
		yAxis.setText(Double.toString(accelerometer.y));
		zAxis.setText(Double.toString(accelerometer.z));

		xRot.setText(Double.toString(gyroscope.xRotation));
		yRot.setText(Double.toString(gyroscope.yRotation));
		zRot.setText(Double.toString(gyroscope.zRotation));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
