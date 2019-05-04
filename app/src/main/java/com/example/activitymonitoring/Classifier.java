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
    private Record[] trainingSet = new Record[number_trainingsset];
    private Record[] testSet = new Record[number_trainingsset];
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
                        trainingSet[i].classLabel = 0;
                        break;
                    case "Upstairs":
                        trainingSet[i].classLabel = 0;
                        break;
                    case "Downstairs":
                        trainingSet[i].classLabel = 0;
                        break;
                    case "Jogging":
                        trainingSet[i].classLabel = 0;
                        break;
                    case "Sitting":
                        trainingSet[i].classLabel = 1;
                        break;
                    case "Standing":
                        trainingSet[i].classLabel = 2;
                        break;
                    default:
                        break;
                }
                scanner.next();
                trainingSet[i].x = Double.parseDouble(scanner.next());
                trainingSet[i].y = Double.parseDouble(scanner.next());
                trainingSet[i].z = Double.parseDouble(scanner.next());
                scanner.nextLine();
            }



        int i = 0;
        while(scanner.hasNextLine())
        {
            scanner.next();
            switch (scanner.next())
            {
                case "Walking":
                    trainingSet[i].classLabel = 1;
                    break;
                case "Upstairs":
                    trainingSet[i].classLabel = 1;
                    break;
                case "Downstairs":
                    trainingSet[i].classLabel = 1;
                    break;
                case "Jogging":
                    trainingSet[i].classLabel = 1;
                    break;
                case "Sitting":
                    trainingSet[i].classLabel = 1;
                    break;
                case "Standing":
                    trainingSet[i].classLabel = 1;
                    break;
                default:
                    break;
            }

            scanner.next();
            testSet[i].x = Double.parseDouble(scanner.next());
            testSet[i].y = Double.parseDouble(scanner.next());
            testSet[i].z = Double.parseDouble(scanner.next());
            scanner.nextLine();
            i++;
        }
    }

    public int predict(Record sample)
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



    private Record[] findKNearestNeighbors(Record[] trainingSet, Record sample)
    {

        Record[] neighbors = new Record[K];

        int index;
        for (index = 0; index < K; index++)
        {
            trainingSet[index].clacDistanc(trainingSet[index], sample);
            neighbors[index] = trainingSet[index];
        }

        //go through the remaining records in the trainingSet to find K nearest neighbors
        for (index = K; index < trainingSet.length; index++)
        {
            trainingSet[index].clacDistanc(trainingSet[index], sample);

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
}
