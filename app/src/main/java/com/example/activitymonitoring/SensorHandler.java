package com.example.activitymonitoring;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;


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
		Sensor sensor = sensorEvent.sensor;
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			accelerometerValuesXAxis.add((double) sensorEvent.values[0]);
			accelerometerValuesYAxis.add((double)sensorEvent.values[1]);
			accelerometerValuesZAxis.add((double)sensorEvent.values[2]);
		}

		//NEEDED FOR A2
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

		if(accelerometerValuesXAxis.size() == Record.WINDOW_SIZE - 1 && accelerometerValuesYAxis.size() >= Record.WINDOW_SIZE - 1 && accelerometerValuesZAxis.size() >= Record.WINDOW_SIZE - 1) {
			records = new Record();
			records.toDoubleArray(accelerometerValuesXAxis, 0);
			records.toDoubleArray(accelerometerValuesYAxis, 1);
			records.toDoubleArray(accelerometerValuesZAxis, 2);
			records.saveDirectionvalues(gyroscopeValues);
			activity.updateEditView(records);
			accelerometerValuesXAxis.clear();
			accelerometerValuesYAxis.clear();
			accelerometerValuesZAxis.clear();
			gyroscopeValues.clear();
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

