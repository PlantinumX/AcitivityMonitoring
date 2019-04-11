package com.example.activitymonitoring;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class SensorHandler implements SensorEventListener
{

	MainActivity activity;
	List<Gyroscope> gyroscopeValues;

	List<Accelerometer> accelerometerValues;

	public  SensorHandler(MainActivity activity)
	{
		this.activity = activity;
		accelerometerValues = new ArrayList<Accelerometer>();
		gyroscopeValues = new ArrayList<>();
	}


	@Override
	public void onSensorChanged(SensorEvent sensorEvent)
	{
		Sensor sensor = sensorEvent.sensor;
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			Accelerometer accelerometer = new Accelerometer();
			accelerometer.x = sensorEvent.values[0];
			accelerometer.y = sensorEvent.values[1];
			accelerometer.z = sensorEvent.values[2];
			accelerometerValues.add(accelerometer);
		}

		if (sensor.getType() == Sensor.TYPE_GYROSCOPE)
		{
			Gyroscope gyroscope = new Gyroscope();
			float[] rotationMatrix = new float[16];
			float[] remappedRotationMatrix = new float[16];

			SensorManager.getRotationMatrixFromVector(rotationMatrix,sensorEvent.values);

			SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);
			float[] orientations = new float[3];
			SensorManager.getOrientation(remappedRotationMatrix,orientations);
			gyroscope.xRotation = Math.toDegrees(orientations[0]);
			gyroscope.yRotation = Math.toDegrees(orientations[1]);
			gyroscope.zRotation = Math.toDegrees(orientations[2]);
			gyroscopeValues.add(gyroscope);
		}
		if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
		{

		}
		if(accelerometerValues.size() > 1 && gyroscopeValues.size() > 1)
		{
			activity.updateEditView(accelerometerValues.get(accelerometerValues.size()-1), gyroscopeValues.get(gyroscopeValues.size() - 1));
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{

	}
}


class Gyroscope
{
	public double xRotation;
	public double yRotation;
	public double zRotation;
	Gyroscope(){}
}


//TODO maybe we should inherit Sensor  class from Android maybe we need it
class Accelerometer
{
	public double x;
	public double y;
	public double z;

	Accelerometer() {}

	Accelerometer(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}



}
