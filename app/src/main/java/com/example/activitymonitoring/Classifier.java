package com.example.activitymonitoring;


import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


//Classifier gets data in Real Time from accelorometer and returns output from pre trained model use it for Localization
public class Classifier
{

    static
    {
        System.loadLibrary("tensorflow_inference");
    }
    private static final String MESSAGE_TAG = "CLASSIFIER";
    private TensorFlowInferenceInterface inferenceInterface;
	private static final String INPUT_NODE = "input";
	private static final String[] OUTPUT_NODES = {"y_"};
	private static final String OUTPUT_NODE = "y_";
	private static final long[] INPUT_SIZE = {1, SensorHandler.WINDOW_SIZE * 3};
	private static final int OUTPUT_SIZE = 3;
	private static final String MODEL_FILE = "file:///android_asset/frozen_activity.pb";
	public Classifier(final Context context)
    {

		try {
			inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
		} catch (Exception e) {
			Log.e(MESSAGE_TAG, e.getStackTrace().toString());
		}
    }

    public float[] predictProbabilities(float[] data)
    {
        float[] result = new float[OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);
        Log.e(MESSAGE_TAG,"RESULTS " + "Sitting "+  Float.toString(result[0]) +" Standing "+ Float.toString(result[1]) +" Jogging " + Float.toString(result[2]));
	    // Sitting = 0
	    // Standing = 1
	    // Jogging = 2
	    return result;
    }

}
