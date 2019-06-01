package com.example.activitymonitoring;


import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.*;
import java.security.KeyStore;
import java.util.Scanner;
import java.util.ArrayList;


//Classifier gets data in Real Time from accelorometer and returns output from pre trained model use it for Localization
public class Classifier {

	private static final String MESSAGE_TAG = "Classifier";
    private static  int NUMBER_TRAININGSSET = 997;
    private Record[] trainingSet = new Record[NUMBER_TRAININGSSET];
//    private Record[] testSet = new Record[NUMBER_TRAININGSSET];
    private final int K = 21;
    private final int NUM_CLASSES = 3;
    public BufferedReader fReader;
	private Scanner scanner;
	//SHOULD GET PREPROCESSED TRAININGS DATA
    public Classifier(Activity activity) throws FileNotFoundException,IOException {
		AssetManager am = activity.getAssets();
		scanner = new Scanner(new InputStreamReader(am.open("data_v3.txt"))); //todo change filename
		readTrainingsSet();


    }

    public float[] predict(Record sample)
    {
	    sample.calculateMeans();
	    sample.calculateVariances();
	    sample.findMaxInAxes();
	    sample.findMinInAxes();
        Record[] neighbors = findKNearestNeighbors(trainingSet, sample);

        float[] labelCounts = new float[NUM_CLASSES];
        for (int index = 0; index < K; index++)
            labelCounts[neighbors[index].classLabel]++;
		labelCounts[0] /= K;
		labelCounts[1] /= K;
		labelCounts[2] /= K;
		return labelCounts;
    }



    private Record[] findKNearestNeighbors(Record[] trainingSet, Record sample)
    {

        Record[] neighbors = new Record[K];

        int index;
        for (index = 0; index < K; index++)
        {
            trainingSet[index].clacDistanc(sample);
            neighbors[index] = trainingSet[index];
        }

        //go through the remaining records in the trainingSet to find K nearest neighbors
        for (index = K; index < trainingSet.length; index++)
        {
            trainingSet[index].clacDistanc(sample);

            int maxIndex = 0;
            for (int i = 1; i < K; i++)
            {
                if (neighbors[i].distance > neighbors[maxIndex].distance)
                {
                    maxIndex = i;
                }
            }

            if (neighbors[maxIndex].distance > trainingSet[index].distance)
            {
                neighbors[maxIndex] = trainingSet[index];
            }
        }

        return neighbors;
    }
	void feedTrainingSet(ArrayList<Record> records)
	{
		int index = 0;
		for(Record record:records)
		{
			record.calculateMeans();
			record.calculateVariances();
			record.findMaxInAxes();
			record.findMinInAxes();
			this.trainingSet[index] = record;
			index++;
		}
		Log.e(MESSAGE_TAG,"Traningsset is read\n");
	}

	private int getLabelNumber(String label)
	{
		switch (label)
		{
			case "Walking":
				return 0;
			case "Sitting":
				return 1;
			case "Standing":
				return 2;
			default:
				return -1;
		}
	}
    private void readTrainingsSet() throws IOException
    {
    	//first read all data in
		ArrayList<ArrayList<String>> windows = new ArrayList<>();
	    String line = scanner.nextLine();
	    int window_index = 0;
	    ArrayList<Record> accelerometerWindows = new ArrayList<>();
	    ArrayList<String> window = new ArrayList<>();
		window.add(line);
	    window_index++;
	    while (line != null )
	    {
	        if(window_index == Record.WINDOW_SIZE)
		    {
			    windows.add(new ArrayList<String>(window));
			    window_index = 0;
			    window.clear();
		    }
		    if(scanner.hasNext())
	        {
		        line = scanner.nextLine();
		        window.add(line);
		        window_index++;
	        }
	        else
	        {
	        	line = null;
	        }

	    }


	    //now skip all Windows which have different labels
		for(int i = 0;i < windows.size(); i++) {
			boolean isWindowValid = true;
			String tmpLine = windows.get(i).get(0);
			int windowLabel = getLabelNumber(tmpLine.split(",")[1]);

			for(int j = 0;j < windows.get(i).size();j++) {
				line = windows.get(i).get(j);
				String[] data = line.split(",");
				if(windowLabel != getLabelNumber(data[1]))
				{
					isWindowValid = false;
				}
			}
			if(isWindowValid)
			{
				Record accelerometerWindow = new Record();
				for(int j = 0;j < windows.get(i).size();j++) {
					line = windows.get(i).get(j);
					String[] data = line.split(",");
					accelerometerWindow.x[j] = Double.parseDouble(data[3]);
					accelerometerWindow.y[j] = Double.parseDouble(data[4]);
					accelerometerWindow.z[j] = Double.parseDouble(data[5]);
					accelerometerWindow.classLabel = getLabelNumber(data[1]);

				}
				accelerometerWindows.add(accelerometerWindow);
			}
		}
		feedTrainingSet(accelerometerWindows);
    }
}
