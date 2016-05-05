
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
	
	public void annTrain(){
		
		 System.out.println("Training ANN");
		 int hiddenLayers= (int)(ATTRIBUTES+this.classes)/2+1;
		 System.out.println("hidden layers:"+hiddenLayers);
		 Mat ann_layers= new Mat(3,1, CvType.CV_32S);
		 ann_layers.put(0,0,ATTRIBUTES);
		 ann_layers.put(1,0,85);
		 ann_layers.put(2,0,this.classes);
		 CvANN_MLP ann= new CvANN_MLP(ann_layers);
		 
		// ann= new CvANN_MLP(layerSizes, activateFunc, fparam1, fparam2)
		 //System.out.print(training_data.dump());
		 CvANN_MLP_TrainParams params= new CvANN_MLP_TrainParams();
		 params.set_term_crit(new TermCriteria(TermCriteria.MAX_ITER+TermCriteria.EPS,10000, 0.001));
		 
		 params.set_train_method( CvANN_MLP_TrainParams.BACKPROP);
		 params.set_bp_dw_scale(0.05);
		 params.set_bp_moment_scale(0.05);
		 
		 //identity flag gains higher accuracy 
		 // for 10 classes and 5 features
		 //8 hidden layers gain high accuracy
		
		 int iterations = ann.train(trainingData, annTrainingClasses,new Mat(),new Mat(),params,CvANN_MLP.IDENTITY);
		 System.out.println("Iterations: "+iterations);
		
		 System.out.println("Saving Network..");
		 ann.save("ann");
		 System.out.println("Done.");
		
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
}
