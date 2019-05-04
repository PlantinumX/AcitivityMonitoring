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
public class Classifier {


    int number_trainingsset = 925936;
    int number_samples = 925936/90;
    private Record[] trainingSet = new Record[3];
    private Record[] testSet = new Record[3];
    private final int K = 21;
    private final int NUM_CLASSES = 3;

    public Classifier(InputStream trainingFile)
    {


            Scanner scanner = new Scanner("actitracker_raw.txt"); //todo change filename
            scanner.useDelimiter(",");


            for(int i = 0; i < number_trainingsset; i++)
            {
                scanner.next();
                switch (scanner.next()) {
                    case "Walking":
                        scanner.next();
                        trainingSet[0].x[i] = Double.parseDouble(scanner.next());
                        trainingSet[0].y[i] = Double.parseDouble(scanner.next());
                        trainingSet[0].z[i] = Double.parseDouble(scanner.next());
                        break;
                    case "Upstairs":
                        scanner.next();
                        trainingSet[0].x[i] = Double.parseDouble(scanner.next());
                        trainingSet[0].y[i] = Double.parseDouble(scanner.next());
                        trainingSet[0].z[i] = Double.parseDouble(scanner.next());
                        break;
                    case "Downstairs":
                        scanner.next();
                        trainingSet[0].x[i] = Double.parseDouble(scanner.next());
                        trainingSet[0].y[i] = Double.parseDouble(scanner.next());
                        trainingSet[0].z[i] = Double.parseDouble(scanner.next());
                        break;
                    case "Jogging":
                        scanner.next();
                        trainingSet[0].x[i] = Double.parseDouble(scanner.next());
                        trainingSet[0].y[i] = Double.parseDouble(scanner.next());
                        trainingSet[0].z[i] = Double.parseDouble(scanner.next());
                        break;
                    case "Sitting":
                        scanner.next();
                        trainingSet[1].x[i] = Double.parseDouble(scanner.next());
                        trainingSet[1].y[i] = Double.parseDouble(scanner.next());
                        trainingSet[1].z[i] = Double.parseDouble(scanner.next());
                        break;
                    case "Standing":
                        scanner.next();
                        trainingSet[2].x[i] = Double.parseDouble(scanner.next());
                        trainingSet[2].y[i] = Double.parseDouble(scanner.next());
                        trainingSet[2].z[i] = Double.parseDouble(scanner.next());
                        break;
                    default:
                        break;
                }
                scanner.nextLine();
            }



        int i = 0;
        while(scanner.hasNextLine())
        {
            scanner.next();
            switch (scanner.next()) {
                case "Walking":
                    scanner.next();
                    testSet[0].x[i] = Double.parseDouble(scanner.next());
                    testSet[0].y[i] = Double.parseDouble(scanner.next());
                    testSet[0].z[i] = Double.parseDouble(scanner.next());
                    break;
                case "Upstairs":
                    scanner.next();
                    testSet[0].x[i] = Double.parseDouble(scanner.next());
                    testSet[0].y[i] = Double.parseDouble(scanner.next());
                    testSet[0].z[i] = Double.parseDouble(scanner.next());
                    break;
                case "Downstairs":
                    scanner.next();
                    testSet[0].x[i] = Double.parseDouble(scanner.next());
                    testSet[0].y[i] = Double.parseDouble(scanner.next());
                    testSet[0].z[i] = Double.parseDouble(scanner.next());
                    break;
                case "Jogging":
                    scanner.next();
                    testSet[0].x[i] = Double.parseDouble(scanner.next());
                    testSet[0].y[i] = Double.parseDouble(scanner.next());
                    testSet[0].z[i] = Double.parseDouble(scanner.next());
                    break;
                case "Sitting":
                    scanner.next();
                    testSet[1].x[i] = Double.parseDouble(scanner.next());
                    testSet[1].y[i] = Double.parseDouble(scanner.next());
                    testSet[1].z[i] = Double.parseDouble(scanner.next());
                    break;
                case "Standing":
                    scanner.next();
                    testSet[2].x[i] = Double.parseDouble(scanner.next());
                    testSet[2].y[i] = Double.parseDouble(scanner.next());
                    testSet[2].z[i] = Double.parseDouble(scanner.next());
                    break;
                default:
                    break;
            }
            scanner.nextLine();
            i++;
        }
    }

    public int predict(Record[] sample)
    {

        Record[] neighbors = findKNearestNeighbors(trainingSet, sample);

        int[] labelCounts = new int[NUM_CLASSES];
        for (int index = 0; index < K; index++)
            labelCounts[neighbors[index].classLabel]++;

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
    private Record[] findKNearestNeighbors(Record[] trainingSet, Record[] sample)
    {

        Record[] neighbors = new Record[K];

        int index;
        for (index = 0; index < K; index++)
        {
            trainingSet[index].distance = Record.clacDistanc(trainingSet[index], sample);
            neighbors[index] = trainingSet[index];
        }

        //go through the remaining records in the trainingSet to find K nearest neighbors
        for (index = K; index < trainingSet.length; index++)
        {
            trainingSet[index].distance = Record.clacDistanc(trainingSet[index], sample);

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
