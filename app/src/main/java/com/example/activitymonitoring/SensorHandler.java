package com.example.activitymonitoring;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;


import java.io.OutputStreamWriter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.Context.*;
import static java.nio.charset.Charset.*;

//Reading from a file

public class SensorHandler implements SensorEventListener {
	public static final int WINDOW_SIZE = 90;
	ClassifierActivity activity;
	List<Gyroscope> gyroscopeValues;

	List<Accelerometer> accelerometerValues;

	public OutputStreamWriter fOutStream;

	public  SensorHandler(ClassifierActivity activity) {
		this.activity = activity;
		accelerometerValues = new ArrayList<>();
		gyroscopeValues = new ArrayList<>();
		try {
		fOutStream = new OutputStreamWriter(activity.openFileOutput("data.txt", MODE_PRIVATE), forName("UTF-8"));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor sensor = sensorEvent.sensor;
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			Accelerometer accelerometer = new Accelerometer(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
			accelerometerValues.add(accelerometer);
		}

		if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			float[] rotationMatrix = new float[16];
			float[] remappedRotationMatrix = new float[16];
			float[] orientations = new float[3];
			SensorManager.getRotationMatrixFromVector(rotationMatrix,sensorEvent.values);
			SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);
			SensorManager.getOrientation(remappedRotationMatrix,orientations);

			Gyroscope gyroscope = new Gyroscope(Math.toDegrees(orientations[0]), Math.toDegrees(orientations[1]), Math.toDegrees(orientations[2]));
			gyroscopeValues.add(gyroscope);
		}
		//TODO do we need compass too ??
		if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)  {

		}
		if(accelerometerValues.size() >= 1) {
			activity.updateEditView(accelerometerValues.get(accelerometerValues.size()-1));
		}
		accelerometerValues.clear();
		gyroscopeValues.clear();

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}


	public void writeToActivityLogFile(float[] result) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
			String format = simpleDateFormat.format(new Date()) + "  ";
			fOutStream.write(format);
			fOutStream.write(Float.toString(result[0]) + " " + Float.toString(result[1])+ " " +
					Float.toString(result[2]) + " " + "MAXIMUM" );
			int index = 0;
			for(int i = 0;i < 3;i++){
				if(result[i] > result[index]) {
					index = i;
				}
			}
			fOutStream.write(" " + index);
			fOutStream.write("\n");
			fOutStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
    	}
	}

	public void writeToActivityLogFile(String selectedActivity,Accelerometer accelerometer) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
			String format = simpleDateFormat.format(new Date());
			fOutStream.write("00,");
			fOutStream.write(selectedActivity+",");
			fOutStream.write(format + ",");
			fOutStream.write(Float.toString(accelerometer.x)
					+"," + Float.toString(accelerometer.y) + ","
					+ Float.toString(accelerometer.z));
			fOutStream.write("\n");
			fOutStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public static float[] convertToFloatArray(List<Float> list)
	{
		int i = 0;
		float[] array = new float[list.size()];

		for (Float f : list) {
			array[i++] = (f != null ? f : Float.NaN);
		}
		return array;
	}

	public static  float[] calculateMeans(float[] data,int window)
	{
		float[] mean = new float[3];
		mean[0] = (float)0.0;
		mean[1] = (float)0.0;
		mean[2] = (float)0.0;
		for(int axis = 0;axis < 3;axis++) {
			for (int i = axis*window; i < (axis+1)*window; i++) {
				mean[axis] += data[i];
			}
			mean[axis] /= window;
		}

		//mean x,mean y, mean z
		return mean;
	}

	public static float[] calculateVariances(float[] data, int window,float[] mean)
	{
		float[] variances = new float[3];
		variances[0] = (float)0.0;
		variances[1] = (float)0.0;
		variances[2] = (float)0.0;
		for(int axis = 0;axis < 3;axis++) {
			for (int i = axis*window; i < (axis+1)*window; i++) {
				variances[axis] += (data[i] - mean[axis]) * (data[i] - mean[axis]);
			}
			variances[axis] /= window;
			variances[axis] = (float) Math.sqrt(variances[axis]);
		}
		return variances;
	}

	public static float[] normalizeData(float[] data, int window, float[] means, float[] variances)
	{
		for(int axis = 0;axis < 3;axis++) {
			for (int i = axis*window; i < (axis+1)*window; i++) {
				data[i] = (data[i] - means[axis] ) / variances[axis];
			}
		}
		return data;
	}

}



class Gyroscope {
	public double xRotation;
	public double yRotation;
	public double zRotation;

	Gyroscope(double x, double y, double z) {
		xRotation = x;
		yRotation = y;
		zRotation = z;
	}

	@Override
	public String toString() {
		return "Gyroscope : X_ROT = " + Double.toString(xRotation) + " Y_ROT = " + Double.toString(yRotation) + " Z_ROT = " + Double.toString(zRotation);
	}
}


//TODO maybe we should inherit Sensor  class from Android maybe we need it
class Accelerometer {
	public transient float  x;
	public transient float y;
	public transient float z;
	Accelerometer() {}

	Accelerometer(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "X = " + Float.toString(x) + " Y = " + Float.toString(y) + " Z = " + Float.toString(z);
	}
}
