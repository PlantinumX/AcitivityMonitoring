package com.example.activitymonitoring;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.text.LoginFilter;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.Context.*;
import static java.nio.charset.Charset.*;

//Reading from a file


public class SensorHandler implements SensorEventListener {
	BaseActivity activity;
	List<Gyroscope> gyroscopeValues;
	private Record records;
	public OutputStreamWriter fOutStream;
	private List<Double> accelerometerValuesXAxis;
	private List<Double> accelerometerValuesYAxis;
	private List<Double> accelerometerValuesZAxis;
	private double orientations;
	long duration = 0;
	float[] gData = new float[3]; // accelerometer
	float[] mData = new float[3]; // magnetometer
	float[] rMat = new float[9];
	float[] iMat = new float[9];
	float[] orientation = new float[3];
	public  SensorHandler(BaseActivity activity) {
		this.activity = activity;
		accelerometerValuesXAxis = new ArrayList<>();
		accelerometerValuesYAxis = new ArrayList<>();
		accelerometerValuesZAxis = new ArrayList<>();
		gyroscopeValues = new ArrayList<>();
		records =  new Record();
		try {
			fOutStream = new OutputStreamWriter(activity.openFileOutput("data.txt", MODE_PRIVATE), forName("UTF-8"));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		if(duration == 0)
		{
			duration = System.currentTimeMillis();
		}

		Sensor sensor = sensorEvent.sensor;
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			accelerometerValuesXAxis.add((double) sensorEvent.values[0]);
			accelerometerValuesYAxis.add((double)sensorEvent.values[1]);
			accelerometerValuesZAxis.add((double)sensorEvent.values[2]);
			gData = sensorEvent.values.clone();
		}

		//NEEDED FOR A2
		if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			mData = sensorEvent.values.clone();
		}


		if ( SensorManager.getRotationMatrix( rMat, iMat, gData, mData ) ) {
			int mAzimuth= (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 -50) % 360;
			orientations = ((double)mAzimuth);
		}

		if(accelerometerValuesXAxis.size() == Record.WINDOW_SIZE - 1 && accelerometerValuesYAxis.size() >= Record.WINDOW_SIZE - 1 && accelerometerValuesZAxis.size() >= Record.WINDOW_SIZE - 1 ) {
			duration = System.currentTimeMillis() - duration;
			records = new Record();
			records.toDoubleArray(accelerometerValuesXAxis, 0);
			records.toDoubleArray(accelerometerValuesYAxis, 1);
			records.toDoubleArray(accelerometerValuesZAxis, 2);
			Log.d("SENSORHANDLER", "AZIMUTH " + orientations);

			records.saveDirectionvalues(gyroscopeValues);
			records.orientation = orientations;
			records.duration = duration;
			activity.updateEditView(records);
			accelerometerValuesXAxis.clear();
			accelerometerValuesYAxis.clear();
			accelerometerValuesZAxis.clear();
			accelerometerValuesZAxis.clear();
			gyroscopeValues.clear();
			duration = 0;
		}


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

	public void writeToActivityLogFile(String selectedActivity/*,Accelerometer accelerometer*/) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
			String format = simpleDateFormat.format(new Date());
			fOutStream.write("00,");
			fOutStream.write(selectedActivity+",");
			fOutStream.write(format + ",");/*
			fOutStream.write(Float.toString(accelerometer.x)
					+"," + Float.toString(accelerometer.y) + ","
					+ Float.toString(accelerometer.z));
			fOutStream.write("\n");*/
			fOutStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

