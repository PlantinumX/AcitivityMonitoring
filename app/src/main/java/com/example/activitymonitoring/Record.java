package com.example.activitymonitoring;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Record {
	public static int WINDOW_SIZE = 20; //20 samples
	public static int AXES_SIZE = 3;
	public Gyroscope[] direction;
	public double[] x;
	public double[] y;
	public double[] z;
	public double[] featureMean;
	public double[] featurePeak;
	public double[] featureMin;
	public double[] featureDev;
	public double[] featureAbsoluteDev;
	public double featureResultant;
	public long duration;
	double distance;
	int classLabel;
	public double orientation;

	//TODO timestamp and user maybe needed

	Record() {
//        this.user = 0;
//        this.timestamp = 0;
		this.x = new double[WINDOW_SIZE];
		this.y = new double[WINDOW_SIZE];
		this.z = new double[WINDOW_SIZE];
		this.direction = new Gyroscope[WINDOW_SIZE];
		this.featureDev = new double[AXES_SIZE];
		this.featureMin = new double[AXES_SIZE];
		this.featurePeak = new double[AXES_SIZE];
		this.featureMean = new double[AXES_SIZE];
		this.featureAbsoluteDev = new double[AXES_SIZE];
		this.featureResultant = 0;
		this.classLabel = 0;
		this.duration = 0;

	}

	void clacDistanc(Record sample) {
		for (int i = 0; i < WINDOW_SIZE; i++) {

			this.distance += Math.pow(x[i] - sample.x[i], 2);
			this.distance += Math.pow(y[i] - sample.y[i], 2);
			this.distance += Math.pow(z[i] - sample.z[i], 2);
		}
		for (int i = 0; i < AXES_SIZE; i++) {
			this.distance += Math.pow(featureMean[i] - sample.featureMean[i], 2);
			this.distance += Math.pow(featurePeak[i] - sample.featurePeak[i], 2);
			this.distance += Math.pow(featureMin[i] - sample.featureMin[i], 2);
			this.distance += Math.pow(featureDev[i] - sample.featureDev[i], 2);
		}
		this.distance += Math.pow(featureResultant - sample.featureResultant, 2);
		this.distance = Math.sqrt(distance);
	}

	public void calculateMeans() {
		featureMean[0] = 0;
		featureMean[1] = 0;
		featureMean[2] = 0;
		for (int i = 0; i < WINDOW_SIZE; i++) {
			featureMean[0] += x[i];
			featureMean[1] += y[i];
			featureMean[2] += z[i];
		}
		featureMean[0] /= WINDOW_SIZE;
		featureMean[1] /= WINDOW_SIZE;
		featureMean[2] /= WINDOW_SIZE;

	}

	/*
		XSTANDDEV, YSTANDDEV, ZSTANDDEV are the standard deviations
		for each axis.
	*/
	public void calculateVariances() {
		featureDev[0] = 0.0f;
		featureDev[1] = 0.0f;
		featureDev[2] = 0.0f;
		for (int i = 0; i < WINDOW_SIZE; i++)
		{
			double x = this.x[i];
			double y = this.y[i];
			double z = this.z[i];
			double xMean = featureMean[0];
			double yMean = featureMean[1];
			double zMean = featureMean[2];

			featureDev[0] += Math.pow((x - xMean),2);
			featureDev[1] += Math.pow((y - yMean),2);
			featureDev[2] += Math.pow((z - zMean),2);
		}
		featureDev[0] /= WINDOW_SIZE - 1; //wikipedia ??
		featureDev[1] /= WINDOW_SIZE - 1; //wikipedia ??
		featureDev[2] /= WINDOW_SIZE - 1; //wikipedia ??
		featureDev[0] = Math.sqrt(featureDev[0]); //wikipedia ??
		featureDev[1] = Math.sqrt(featureDev[1]); //wikipedia ??
		featureDev[2] = Math.sqrt(featureDev[2]); //wikipedia ??
	}

//	public static float[] normalizeData(float[] data, int window, float[] means, float[] variances)
//	{
//		for(int axis = 0;axis < 3;axis++) {
//			for (int i = axis*window; i < (axis+1)*window; i++) {
//				data[i] = (data[i] - means[axis] ) / variances[axis];
//			}
//		}
//		return data;
//	}

	//XPEAK,YPEAK,ZPEAK
	public void findMaxInAxes() {

		for (int i = 0;i < this.x.length;i++) {
			if(this.x[i] >= this.featurePeak[0])
				this.featurePeak[0] = this.x[i];

			if(this.y[i] >= this.featurePeak[1])
				this.featurePeak[1] = this.y[i];


			if(this.z[i] >= this.featurePeak[2])
				this.featurePeak[2] = this.z[i];

		}

	}

	//XMIN,YMIN,ZMIN
	public void findMinInAxes() {
		this.featurePeak[0] = this.x[0];
		this.featurePeak[1] = this.y[0];
		this.featurePeak[2] = this.z[0];
		for (int i = 0;i < this.x.length;i++) {

			if(this.x[i] <= this.featurePeak[0])
				this.featurePeak[0] = this.x[i];

			if(this.y[i] <= this.featurePeak[1])
				this.featurePeak[1] = this.y[i];


			if(this.z[i] <= this.featurePeak[2])
				this.featurePeak[2] = this.z[i];

		}
	}

	//FOLLOWING METHODS SHOULD BE IMPLEMENTED
	//XABSOLDEV, YABSOLDEV, ZABSOLDE
	public void calculateAbsoluteDeviation() {

	}

	//RESULTANT is the average of the square roots of the sum of the values
	//	of each axis squared √(xi^2 + yi^2 + zi^2).
	public void calculateResultant() {

	}

	public void toDoubleArray(List<Double> list,int type) //TODO if enough time code cleanup
	{
		if (type == 0)
		{
			for (int i = 0; i < list.size(); i++) {
				this.x[i] = list.get(i);  // java 1.4 style
			}
		}
		else if(type == 1)
		{
			for (int i = 0; i < list.size(); i++) {
				this.y[i] = list.get(i);  // java 1.4 style
			}
		}
		else if (type == 2)
		{
			for (int i = 0; i < list.size(); i++) {
				this.z[i] = list.get(i);  // java 1.4 style
			}
		}

	}

	public void saveDirectionvalues(List<Gyroscope> gyroscope)
	{
		for (int i = 0; i < gyroscope.size(); i++) {
			this.direction[i] = gyroscope.get(i);  // java 1.4 style
		}

	}

	public  static  double calculateEuclidenDistance(Position current, Position lastPos) {
		return Math.sqrt(Math.pow(current.getX() - lastPos.getX(),2) + Math.pow(current.getY() - lastPos.getY(),2));
	}
}


