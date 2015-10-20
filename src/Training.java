
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
public class Training {
	private static final int ATTRIBUTES = 5;
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
	
	double[] svmPredicted, bayesPredicted, classifications;
	
	int trainingSamples, classes, testingSamples;
	CvSVM svm;
	TrainPanel trainpanel;
	public Training(int classes, int trainingSamples){
		//read file

		this.trainingSamples= trainingSamples;
		this.classes= classes;
		this.trainingData= new Mat(this.trainingSamples,ATTRIBUTES,CvType.CV_32F);
		this.annTrainingClasses= new Mat();
		this.bayesTrainingClasses= new Mat(this.trainingSamples,1,CvType.CV_32F);
	   	this.annTrainingClasses= Mat.zeros(this.trainingSamples,this.classes,CvType.CV_32F);
	   	this.readFile("training_data.txt", trainingData, annTrainingClasses, bayesTrainingClasses);
	   	
	   	System.out.println(bayesTrainingClasses.dump());
		//instatiate the Mats
		//count data	
		//train
	   	
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
		CvNormalBayesClassifier bayes= new CvNormalBayesClassifier();
		bayes.train(trainingData, bayesTrainingClasses);
		bayes.save("bayes"); 
		System.out.println("Training Bayes Network Done.");
	}

	/**
	 * 	Trains the data using the livsvm library
	 *  it reads the data from training_data_svm.txt
	 *  then save the network in the file "svm"
	 * 	
	 * */
	public void svmTrain(){
		//this.status.append("Training SVM...\n");
		String[] arguments= "-s 0 -b 1 training_data_svm.txt".split(" ");
		try {
			Svm_train trainSvm= new Svm_train(arguments);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Training SVM Done");
	}
	
}
