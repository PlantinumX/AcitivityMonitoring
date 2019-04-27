package com.example.activitymonitoring;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;




public class MainActivity extends AppCompatActivity
{


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_main_activity);
		Button startButton = (Button) findViewById(R.id.start_button_activity);
		View.OnClickListener mHandler = new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent myIntent = new Intent(MainActivity.this,ClassifierActivity.class);
				MainActivity.this.startActivity(myIntent);
			}
		};
		startButton.setOnClickListener(mHandler);
//
//		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//		sensorHandler = new SensorHandler(this);
//		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) , SensorManager.SENSOR_DELAY_NORMAL);
//		sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) , SensorManager.SENSOR_DELAY_NORMAL);
	}





	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
