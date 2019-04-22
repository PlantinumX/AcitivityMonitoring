package com.example.activitymonitoring;


import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import android.content.Context;

//Classifier gets data in Real Time from accelorometer and returns output from pre trained model
public class Classifier
{

    static
    {
        System.loadLibrary("tensorflow_inference");
    }

    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "D:\\Dokumente\\Studium\\8.Semester\\MC\\Project\\AcitivityMonitoring\\ActivityClassifier\\exported_modell\\saved_model.pb";    //todo change fucking path to tired today FUCK YOU
    private static final String INPUT_NODE = "x_data";
    private static final String[] OUTPUT_NODES = {"y_data"};
    private static final String OUTPUT_NODE = "y_data";
    private static final long[] INPUT_SIZE = {1, 200, 3};
    private static final int OUTPUT_SIZE = 6;

    public Classifier(final Context context)
    {
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
    }

    public int predictProbabilities(double[] data)
    {
        double[] result = new double[OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE); //todo maybe change data from double to float
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);

        double highest_probability = 0;
        int highest_probability_index = 0;
        for(int i = 0; i < result.length; i++)
        {
            if(highest_probability < result[i])
            {
                highest_probability = result[i];
                highest_probability_index = i;
            }
        }

        //Walking = 0
        // Jogging = 1
        // Sitting = 2
        // Standing = 3
        // Upstairs	= 4
        // Downstairs = 5
        return highest_probability_index;
    }

}
