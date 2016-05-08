import weka.core.AbstractInstance;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.neural.*;
import weka.classifiers.functions.supportVector.*;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.classifiers.functions.*;

import java.util.ArrayList;
import java.util.Random;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
import org.opencv.ml.*;


import java.util.*;
import java.io.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
public class Training implements Constants{
//	private static final int ATTRIBUTES = 20;
//	private static final int TRAINING_SAMPLES = 1103; 
//	private static final int TESTING_SAMPLES = 320; 
//	private static final int CLASSES = 10; 

	/**
	 * annTrainingClasses  	- Mat[trainingSamples][classes]
	 * bayesTrainingClasses - Mat[trainingSamples]
	 * 
	 * **/
	public Mat trainingData;
	private Mat annTrainingClasses, bayesTrainingClasses;
	private Mat actual, predicted, classificationResult;
	Instances trainingdata;
	double[] svmPredicted, bayesPredicted, classifications;
	double[] trainingTime;
	int trainingSamples, classes, testingSamples;
	CvSVM svm;
	TrainPanel trainpanel;
	public Training(int classes, int trainingSamples,TestingPanel panel) throws IOException{
		trainingTime = new double[3];
		//read file
		BufferedReader reader = new BufferedReader(new FileReader("training_data.arff"));
		this.trainingdata = new Instances(reader);
		trainingdata.setClassIndex(13);
		reader.close();
		
		this.trainingSamples= trainingSamples;
		this.classes= classes;
		this.trainingData= new Mat(this.trainingSamples,ATTRIBUTES,CvType.CV_32F);
		this.annTrainingClasses= new Mat();
		this.bayesTrainingClasses= new Mat(this.trainingSamples,1,CvType.CV_32F);
	   	this.annTrainingClasses= Mat.zeros(this.trainingSamples,this.classes,CvType.CV_32F);
	   	this.readFile("training_data.txt", trainingData, annTrainingClasses, bayesTrainingClasses);
	   	
	   	//System.out.println(bayesTrainingClasses.dump());
		System.out.println("training samples: "+trainingSamples);
	   	annTrain();
	   	//predictAnn();
	   	bayesTrain();
	   	svmTrain();
	}
	
	private void readFile(String file, Mat trainingData, Mat trainingClasses,Mat trainingClasses2 ){
		String  line = null;
		System.out.println(this.trainingSamples);
		try{
			// open input stream test.txt for reading purpose.
			FileReader fl = new FileReader(file);
			BufferedReader br = new BufferedReader(fl);
			int samples=this.trainingSamples;
			for(int i = 0; i< samples; i++){
				line = br.readLine();
				String[] data = line.split(",");
				for(int j = 0; j <= ATTRIBUTES; j++ ){
					if(j< ATTRIBUTES)
						trainingData.put(i, j,Float.parseFloat(data[j]));
					else{
						int index= Integer.parseInt(data[j])-1;
						trainingClasses.put(i,index , 1);
						trainingClasses2.put(i,0, Integer.parseInt(data[j]));
					}		
				}
				
			}
			br.close();
	  }catch(Exception ex){
	     ex.printStackTrace();
	  }
	}
	
	/**
	 *  Trains the data using the opencv built in Bayes Classifier
	 *  then saves the network in a file named "bayes"
	 * */
	public void bayesTrain(){
		System.out.println("Training BN...");
		NaiveBayes nb = new NaiveBayes();
		long startTime = System.nanoTime();
		try {
			nb.buildClassifier(trainingdata);
			double runningTimeNB = (System.nanoTime() - startTime)/1000000;
			runningTimeNB /= 1000;
			trainingTime[2] = runningTimeNB;
			//saving the naive bayes model 
			weka.core.SerializationHelper.write("naivebayes.model", nb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Training Bayes Network Done.");
	}

	/**
	 * 	Trains the data using the svm inweka
	 *  it reads the data from training_data_svm.txt
	 *  then save the network in the file "svm"
	 * 	
	 * */
	public void svmTrain(){
		//this.status.append("Training SVM...\n");
		SMO svm = new SMO();
		
		long startTime = System.nanoTime();
		try {
			startTime = System.nanoTime();
			svm.buildClassifier(trainingdata);
			long runningTimeSVM = (System.nanoTime() - startTime)/1000000;
			runningTimeSVM /= 1000;
			trainingTime[1] = runningTimeSVM;
			weka.core.SerializationHelper.write("svm.model", svm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void annTrain(){
		
		 System.out.println("Training ANN");
		 MultilayerPerceptron mlp = new MultilayerPerceptron();
		 try {
			mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a"));
			long startTime = System.nanoTime();
			mlp.buildClassifier(trainingdata);
			long runningTimeANN = (System.nanoTime() - startTime)/1000000;
			runningTimeANN /= 1000;
			trainingTime[0] = runningTimeANN;
			weka.core.SerializationHelper.write("mlp.model", mlp);
			System.out.println("Done.");
			
 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	public void predictAnn(){
		 CvANN_MLP ann= new CvANN_MLP();
		 ann.load("ann");
		 this.testingSamples= this.trainingSamples;
		 Mat classificationResults= new Mat(1, this.classes,CvType.CV_64F);
		 double classifications2[]= new double[this.testingSamples];
		 double result[]= new double[this.testingSamples];
		 for(int a=0;a< testingSamples;a++){
			 ann.predict(trainingData.row(a), classificationResults);
			 result[a]=getMaximum(classificationResults);
			 System.out.println(classificationResults.dump());
			 //classifications2[a]= actual.get(a, 0)[0];
		 }
		 
		 //this.computeAccuracy(classifications2, result);
	}
	
	 private int getMaximum(Mat classificationResult){
		 Core.MinMaxLocResult mmr = Core.minMaxLoc(classificationResult);
		 return (int)mmr.maxLoc.x+1;
	 }

	public double[] getAllBuildTime() {
		return trainingTime;
		// TODO Auto-generated method stub

	}
}
