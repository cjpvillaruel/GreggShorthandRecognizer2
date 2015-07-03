
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
	private static final int ATTRIBUTES = 4;
//	private static final int TRAINING_SAMPLES = 1103; 
//	private static final int TESTING_SAMPLES = 320; 
//	private static final int CLASSES = 10; 
	
	private Mat training_data, testing_data;
	private Mat training_classes, testing_classes, training_classes2;
	private Mat actual, predicted, classificationResult;
	
	double[] svmPredicted, bayesPredicted, classifications;
	
	int trainingSamples, classes, testingSamples;
	CvSVM svm;
	
	public ML(int training_samples, int classes, int testing_samples){
		//this.initializeData();
//		try{
//			svm= new CvSVM();
//			svm.load("svm");
//			
//		}catch(Exception e){
			this.trainingSamples= training_samples;
			this.classes= classes;
			this.testingSamples= testing_samples;
			this.initializeData();
		//}
	}
	
	public void initializeData(){
		
		this.training_data= new Mat(this.trainingSamples,ATTRIBUTES,CvType.CV_32F);
	   	this.training_classes= new Mat();
	   	this.testing_classes= new Mat(this.testingSamples,this.classes,CvType.CV_32F);
	   	this.testing_data= new Mat(this.testingSamples,ATTRIBUTES,CvType.CV_32F);
	   	this.actual= new Mat(this.testingSamples,1,CvType.CV_32F);
	   	this.training_classes2= new Mat(this.trainingSamples,1,CvType.CV_32F);
	   	this.training_classes= Mat.zeros(this.trainingSamples,this.classes,CvType.CV_32F);
	    this.readFile("training_data.txt", training_data, training_classes, training_classes2, true);
		this.readFile("testing_data.txt", testing_data, testing_classes, actual, false);
		this.classificationResult= new Mat(1,classes,CvType.CV_32F);
		

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
	public void svm(){
		CvSVMParams param = new CvSVMParams();
		param.set_svm_type(CvSVM.C_SVC);
		param.set_kernel_type(CvSVM.RBF);
		param.set_degree(0); // for poly
		param.set_gamma(20); //for poly/rbf/sigmoid
		param.set_coef0(0); ; // for poly/sigmoid
		
		param.set_C(7); // for CV_SVM_C_SVC, CV_SVM_EPS_SVR and CV_SVM_NU_SVR
		param.set_nu(0);  // for CV_SVM_NU_SVC, CV_SVM_ONE_CLASS, and CV_SVM_NU_SVR
		param.set_p(0.0);; // for CV_SVM_EPS_SVR
		
		TermCriteria criteria= new TermCriteria();
		criteria.type= TermCriteria.MAX_ITER +TermCriteria.EPS;
		criteria.maxCount= 1000;
		criteria.epsilon = 1e-6;
		param.set_term_crit(criteria);
		
		svm = new CvSVM(training_data,training_classes2, new Mat(), new Mat(), param);
		//System.out.println(testing_data.dump());
		classifications= new double[this.testingSamples];
		this.svmPredicted= new double[this.testingSamples];
		
		
		for(int i=0;i<this.testingSamples;i++){
			svmPredicted[i]=svm.predict(testing_data.row(i));
			classifications[i]= actual.get(i, 0)[0];
		}
		this.computeAccuracy(classifications, svmPredicted);
	}
	
	public void computeAccuracy(double[] classification, double[] result){
		int i,correct=0,incorrect=0;
		for(i=0;i< classification.length;i++){
			if(classification[i]== result[i]){
				correct++;
			}else incorrect++;
		}
		System.out.println((float)correct/(correct+incorrect)*100);
	}
	public void ann(){
		 Mat ann_layers= new Mat(3,1, CvType.CV_32S);
		 ann_layers.put(0,0,ATTRIBUTES);
		 ann_layers.put(1,0,20);
		 ann_layers.put(2,0,this.classes);
		 CvANN_MLP ann= new CvANN_MLP(ann_layers);
		
		// System.out.print(training_matrix_class.dump());
		 CvANN_MLP_TrainParams params= new CvANN_MLP_TrainParams();
		 params.set_term_crit(new TermCriteria(TermCriteria.MAX_ITER+TermCriteria.EPS,2000, 0.00001f));
		 params.set_train_method( CvANN_MLP_TrainParams.BACKPROP);
		 params.set_bp_dw_scale(0.05);
		 params.set_bp_moment_scale(0.05);
		 
		 int iterations = ann.train(training_data, training_classes,new Mat(),new Mat(),params,CvANN_MLP.NO_INPUT_SCALE);
		 System.out.print(iterations);
		 //System.out.println(this.training_data.dump());
		 ann.save("ann");
		
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
			 System.out.println(classificationResults.dump());
			 classifications2[a]= actual.get(a, 0)[0];
		 }
		 
		 this.computeAccuracy(classifications2, result);
	}
	
//	 public int predict(int index){
////		 ann.load("ann");
////		 ann.predict(testing_matrix.row(index), classificationResult);
////		 //System.out.print(classificationResult.dump());
////		 return getMaximum();
//	 }
	 private int getMaximum(Mat classificationResult){
		 int index=0;
		 double max=0;
		 for(int i=0;i<this.classes;i++){
			 if(classificationResult.get(0, i)[0] > index){
				 index= i;
				 max= classificationResult.get(0, i)[0];
			 }
		 }
		 return index+1;
	 }
	
	public void bayes(){
		 CvNormalBayesClassifier bayes= new CvNormalBayesClassifier();
		 bayes.train(training_data, training_classes2);
		 System.out.println(bayes.predict(testing_data.row(2)));
			double classifications[]= new double[this.testingSamples];
			bayesPredicted= new double[this.testingSamples];
			for(int i=0;i<this.testingSamples;i++){
				bayesPredicted[i]=bayes.predict(testing_data.row(i));
				classifications[i]= actual.get(i, 0)[0];	
			}
			this.computeAccuracy(classifications, bayesPredicted);
	}
	
}
