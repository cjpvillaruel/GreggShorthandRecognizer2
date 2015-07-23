
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
public class ML {
	private static final int ATTRIBUTES = 5;
//	private static final int TRAINING_SAMPLES = 1103; 
//	private static final int TESTING_SAMPLES = 320; 
//	private static final int CLASSES = 10; 
	
	public Mat training_data, testing_data;
	private Mat training_classes, testing_classes, training_classes2, testing_classes2;
	private Mat actual, predicted, classificationResult;
	
	double[] svmPredicted, bayesPredicted, classifications;
	
	int trainingSamples, classes, testingSamples;
	CvSVM svm;
	TrainPanel trainpanel;
	public ML(){
		
	}
	public ML(int training_samples,int testing_samples, int classes){
		this.classes= classes;
		this.trainingSamples= training_samples;
		this.testingSamples= testing_samples;
		this.initializeData();
		this.annTrain();
		this.predictAnn();
	}
	public ML(int testing_samples, int classes){
			this.classes= classes;
			this.testingSamples= testing_samples;
			this.testing_data= new Mat(this.testingSamples,ATTRIBUTES,CvType.CV_32F);
			this.testing_classes= new Mat();
			this.testing_classes2= new Mat(this.testingSamples,1,CvType.CV_32F);
		   	this.testing_classes= Mat.zeros(this.testingSamples,this.classes,CvType.CV_32F);
		   	
			this.readFile("testing_data.txt", testing_data, testing_classes, testing_classes2, false);
			this.predictData();
	}
	public ML(int training_samples, int classes, TrainPanel trainpanel){
		this.trainpanel = trainpanel;
		this.trainingSamples= training_samples;
		this.classes= classes;
		this.training_data= new Mat(this.trainingSamples,ATTRIBUTES,CvType.CV_32F);
		this.training_classes= new Mat();
		this.training_classes2= new Mat(this.trainingSamples,1,CvType.CV_32F);
	   	this.training_classes= Mat.zeros(this.trainingSamples,this.classes,CvType.CV_32F);
	   	this.readFile("training_data.txt", training_data, training_classes, training_classes2, true);
	   	
	   	this.annTrain();
	   	this.svmTrain();
	   	this.bayesTrain();
	   	
	}

	
	public void initializeData(){
		
		this.training_data= new Mat(this.trainingSamples,ATTRIBUTES,CvType.CV_32F);
	   	this.training_classes= new Mat();
	   	this.testing_classes= new Mat(this.testingSamples,this.classes,CvType.CV_32F);
	   	this.testing_data= new Mat(this.testingSamples,ATTRIBUTES,CvType.CV_32F);
	   	this.actual= new Mat(this.testingSamples,1,CvType.CV_32F);
	   	this.training_classes2= new Mat(this.trainingSamples,1,CvType.CV_32F);
	   	this.training_classes= Mat.zeros(this.trainingSamples,this.classes,CvType.CV_32F);
	    
		this.classificationResult= new Mat(1,classes,CvType.CV_32F);
		this.testing_classes2= new Mat(this.testingSamples,1,CvType.CV_32F);
		this.readFile("training_data.txt", training_data, training_classes, training_classes2, true);
		this.readFile("testing_data.txt", testing_data, testing_classes, testing_classes2, false);
		//System.out.println(training_data.dump());
	}
	
	 public void readFile2(String e, Mat set, Mat set_classes, Mat classes2, boolean isTraining){
		 String  line = null;
			try{
				// open input stream test.txt for reading purpose.
				FileReader fl = new FileReader(e);
				BufferedReader br = new BufferedReader(fl);
				
				int samples= isTraining? this.trainingSamples:this.testingSamples;
				for(int i = 0; i< samples; i++){
					line = br.readLine();
					String[] data = line.split(" ");
					//store class
					int index= (int)Double.parseDouble(data[0])-1;
					set_classes.put(i,index , 1);
					classes2.put(i,0, (int)Double.parseDouble(data[0]));
					
					//store attributes
					for(int j=1;j<data.length;j++){
						String[] data2= data[j].split(":");
						set.put(i, (j-1),Double.parseDouble(data2[1]));
					}
				}
				br.close();
		  }catch(Exception ex){
		     ex.printStackTrace();
		  }
	 }
	 public void readFile(String e, Mat set, Mat set_classes, Mat classes2, boolean isTraining){
			String  line = null;
			try{
				// open input stream test.txt for reading purpose.
				FileReader fl = new FileReader(e);
				BufferedReader br = new BufferedReader(fl);
				
				int samples= isTraining? this.trainingSamples:this.testingSamples;
				for(int i = 0; i< samples; i++){
					line = br.readLine();
					String[] data = line.split(",");
					for(int j = 0; j <= ATTRIBUTES; j++ ){
						if(j< ATTRIBUTES)
							set.put(i, j,Float.parseFloat(data[j]));
						else{
								int index= Integer.parseInt(data[j])-1;
								set_classes.put(i,index , 1);
								classes2.put(i,0, Integer.parseInt(data[j]));
						}		
					}
					//System.out.println(i);
				}
				br.close();
		  }catch(Exception ex){
		     ex.printStackTrace();
		  }
		 }
	 
	 
	public void svmTrain(){
		//this.status.append("Training SVM...\n");
		String[] arguments= "-s 0 -b 1 training_data_svm.txt".split(" ");
		try {
			Svm_train trainSvm= new Svm_train(arguments);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//this.status.append("Done.\n");
	}
	
	public void computeAccuracy(double[] classification, double[] result){
		int i,correct=0,incorrect=0;
		for(i=0;i< classification.length;i++){
			if(classification[i]== result[i]){
				++correct;
			}else ++incorrect;
			
			//System.out.println("result "+classification[i]+" "+result[i]);
		}
		System.out.println((float)correct/(correct+incorrect)*100);
		System.out.println(correct+"//"+(correct+incorrect));
	}
	public void annTrain(){
		
		 System.out.println("Training ANN");
		 Mat ann_layers= new Mat(3,1, CvType.CV_32S);
		 ann_layers.put(0,0,ATTRIBUTES);
		 ann_layers.put(1,0,21);
		 ann_layers.put(2,0,this.classes);
		 CvANN_MLP ann= new CvANN_MLP(ann_layers);
		
		// System.out.print(training_matrix_class.dump());
		 CvANN_MLP_TrainParams params= new CvANN_MLP_TrainParams();
		 params.set_term_crit(new TermCriteria(TermCriteria.MAX_ITER+TermCriteria.EPS,5000, 0.00001f));
		 params.set_train_method( CvANN_MLP_TrainParams.BACKPROP);
		 params.set_bp_dw_scale(0.05);
		 params.set_bp_moment_scale(0.05);
		 
		 int iterations = ann.train(training_data, training_classes,new Mat(),new Mat(),params,CvANN_MLP.NO_INPUT_SCALE);
		 System.out.println("Iterations: "+iterations);
		// System.out.println(this.testing_data.dump());
		 System.out.println("Saving Network..");
		 ann.save("ann");
		 
		 System.out.println("Done.");
		
	}
	
	
	public void predictAnn(){
		 CvANN_MLP ann= new CvANN_MLP();
		 ann.load("ann");
		 Mat classificationResults= new Mat(1, this.classes,CvType.CV_64F);
		 double classifications2[]= new double[this.testingSamples];
		 double result[]= new double[this.testingSamples];
		 for(int a=0;a< testingSamples;a++){
			 ann.predict(testing_data.row(a), classificationResults);
			 result[a]=getMaximum(classificationResults);
			// System.out.println(classificationResults.dump());
			 classifications2[a]= actual.get(a, 0)[0];
		 }
		 
		 this.computeAccuracy(classifications2, result);
	}
	

	 private int getMaximum(Mat classificationResult){
		 Core.MinMaxLocResult mmr = Core.minMaxLoc(classificationResult);
		 return (int)mmr.maxLoc.x+1;
	 }
	
	public void bayes(){
		 
		CvNormalBayesClassifier bayes= new CvNormalBayesClassifier();
		 bayes.train(training_data, training_classes2);
		 bayes.save("bayes");
		 System.out.println(bayes.predict(testing_data.row(2)));
			double classifications[]= new double[this.testingSamples];
			bayesPredicted= new double[this.testingSamples];
			for(int i=0;i<this.testingSamples;i++){
				bayesPredicted[i]=bayes.predict(testing_data.row(i));
				classifications[i]= actual.get(i, 0)[0];	
			}
			this.computeAccuracy(classifications, bayesPredicted);
	}
	public void bayesTrain(){
		System.out.println("Training BN...");
		CvNormalBayesClassifier bayes= new CvNormalBayesClassifier();
		 bayes.train(training_data, training_classes2);
		 System.out.println("Saving network...");
		 bayes.save("bayes");
		 
		 System.out.println("Done.");
	}
	private void predictData(){
		System.out.println(testing_classes2.dump());
		double[] resultsANN= new double[testingSamples];
		double[] resultsBN =new double[testingSamples];
		double[] resultsSVM= new double[testingSamples];
		double[] classifications2= new double[testingSamples];
		for(int i=0;i< this.testing_data.rows();i++){
			classifications2[i]= (int)testing_classes2.get(i, 0)[0];
			try {
				double results[]= predict(testing_data.row(i),(int)classifications2[i] );
				resultsANN[i]= results[0];
				resultsSVM[i]= results[1];
				resultsBN[i]= results[2];
				System.out.println(" ANN: "+results[0]+" BN: "+results[2]+" SVM: "+results[1]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("ANN:");
		computeAccuracy( classifications2, resultsANN);
		System.out.println("BN:");
		computeAccuracy( classifications2, resultsBN);
		System.out.println("SVM:");
		computeAccuracy( classifications2, resultsSVM);

	}
	
	
	public double[] predict(Mat data, int classification) throws IOException{
		//load ann

		 double results[]= new double[3];
		 CvANN_MLP ann= new CvANN_MLP();
		 ann.load("ann");
		 Mat classificationResults= new Mat(1, this.classes,CvType.CV_64F);
		 ann.predict(data, classificationResults);
		 results[0]=getMaximum(classificationResults);
		//load svm
		 String input= classification+" ";
		 for(int i=0;i<data.cols();i++){
			 input+= (i+1)+":"+data.get(0, i)[0]+" ";
		 }
		 Svm_predict a = new Svm_predict();
		 results[1]= (int)a.Svm_predict("training_data_svm.txt.model",input);
		//load bayes
		 CvNormalBayesClassifier bayes= new CvNormalBayesClassifier();
		 bayes.load("bayes");
		 results[2]= (int)bayes.predict(data);
		 
		 return results;
		
	}
	
	
	
}
