package com.example.activitymonitoring;


import java.io.*;
import java.util.Scanner;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;


//Classifier gets data in Real Time from accelorometer and returns output from pre trained model use it for Localization
public class Classifier
{

//    private TrainRecord[] trainingSet;
    private List<Double> TrainRecord = new ArrayList<>();
    private final int K = 21;
    private final int NUM_CLASSES = 6;

    public Classifier()
    {

        try
        {
            //read trainingSet and testingSet
            //trainingSet = FileManager.readTrainFile(trainingFile);

            File file = new File(fileName);
            Scanner scanner = new Scanner(file).useLocale(Locale.US);

            //read file
            int NumOfSamples = scanner.nextInt();
            int NumOfAttributes = scanner.nextInt();
            int LabelOrNot = scanner.nextInt();
            scanner.nextLine();

            assert LabelOrNot == 1 : "No classLabel";// ensure that C is present in this file


            //transform data from file into TrainRecord objects
            TrainRecord[] records = new TrainRecord[NumOfSamples];
            int index = 0;
            while(scanner.hasNext()){
                double[] attributes = new double[NumOfAttributes];
                int classLabel = -1;

                //Read a whole line for a TrainRecord
                for(int i = 0; i < NumOfAttributes; i ++){
                    attributes[i] = scanner.nextDouble();
                }

                //Read classLabel
                classLabel = (int) scanner.nextDouble();
                assert classLabel != -1 : "Reading class label is wrong!";

                records[index] = new TrainRecord(attributes, classLabel);
                index ++;
            }

            return records;





        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int predict(double[] sample) {

        TrainRecord[] neighbors = findKNearestNeighbors(trainingSet, sample);

        int[] labelCounts = new int[NUM_CLASSES];
        for (int j = 0; j < K; j++)
            labelCounts[neighbors[j].classLabel]++;

        int predictedLabel = 0;
        int maxCount = -1;
        for (int j = 0; j < NUM_CLASSES; j++)
            if (labelCounts[j] > maxCount) {
                maxCount = labelCounts[j];
                predictedLabel = j;
            }

        return predictedLabel;
    }

    // Find K nearest neighbors of sample within trainingSet
    private TrainRecord[] findKNearestNeighbors(TrainRecord[] trainingSet, double[] sample) {

        TrainRecord[] neighbors = new TrainRecord[K];

        //initialization, put the first K trainRecords into the above arrayList
        int index;
        for (index = 0; index < K; index++) {
            trainingSet[index].distance = metric.getDistance(trainingSet[index].attributes, sample);
            neighbors[index] = trainingSet[index];
        }

        //go through the remaining records in the trainingSet to find K nearest neighbors
        for (index = K; index < trainingSet.length; index++) {
            trainingSet[index].distance = metric.getDistance(trainingSet[index].attributes, sample);

            //get the index of the neighbor with the largest distance to sample
            int maxIndex = 0;
            for (int i = 1; i < K; i++) {
                if (neighbors[i].distance > neighbors[maxIndex].distance)
                    maxIndex = i;
            }

            //add the current trainingSet[index] into neighbors if applicable
            if (neighbors[maxIndex].distance > trainingSet[index].distance)
                neighbors[maxIndex] = trainingSet[index];
        }

        return neighbors;
    }
}


