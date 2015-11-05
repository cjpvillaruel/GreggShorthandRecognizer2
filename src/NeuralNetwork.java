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

public class NeuralNetwork {
	private static final int ATTRIBUTES = 5;
	private Mat training_data, testing_data;
	private Mat training_classes, training_classes2, testing_classes, actual;
	
	private int trainingSamples=2400, testingSamples=2400;
	private int classes= 30;
	
	
	public NeuralNetwork(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		this.training_data= new Mat(this.trainingSamples,ATTRIBUTES,CvType.CV_32F);
	   	this.training_classes= new Mat();
	   	this.testing_classes= new Mat(this.testingSamples,this.classes,CvType.CV_32F);
	   	this.testing_data= new Mat(this.testingSamples,ATTRIBUTES,CvType.CV_32F);
	   	this.actual= new Mat(this.testingSamples,1,CvType.CV_32F);
	   	this.training_classes2= new Mat(this.trainingSamples,1,CvType.CV_32F);
	   	this.training_classes= Mat.zeros(this.trainingSamples,this.classes,CvType.CV_32F);
	   	
		this.readFile("normalized.txt", training_data, training_classes, training_classes2, true);
		this.readFile("normalized.txt", testing_data, testing_classes, actual, false);
		
		System.out.println(testing_data.dump());
		
		 Mat ann_layers= new Mat(3,1, CvType.CV_32S);
		 ann_layers.put(0,0,ATTRIBUTES);
		 ann_layers.put(1, 0, 21);
		 ann_layers.put(2,0,this.classes);
		 CvANN_MLP ann= new CvANN_MLP(ann_layers);
		
		// System.out.print(training_matrix_class.dump());
		 CvANN_MLP_TrainParams params= new CvANN_MLP_TrainParams();
		 params.set_term_crit(new TermCriteria(TermCriteria.MAX_ITER+TermCriteria.EPS,5000, 0.00001));
		 params.set_train_method( CvANN_MLP_TrainParams.BACKPROP);
		 params.set_bp_dw_scale(0.05);
		 params.set_bp_moment_scale(0.05);
		 
		 int iterations = ann.train(training_data, training_classes,new Mat(),new Mat(),params,CvANN_MLP.NO_INPUT_SCALE);
		 System.out.print(iterations);
		 
		 ann.save("neural.txt");
		 Mat classificationResults= new Mat(1, this.classes,CvType.CV_64F);
		 double classifications2[]= new double[this.testingSamples];
		 double result[]= new double[this.testingSamples];
		 for(int a=0;a< testingSamples;a++){
			 ann.predict(testing_data.row(a), classificationResults);
			 result[a]=getMaximum(classificationResults);
			 System.out.println(result[a]);
			 //System.out.println(classificationResults.dump());
			 classifications2[a]= actual.get(a, 0)[0];
		 }
		 
		 computeAccuracy(classifications2, result);
		
	}
	public void computeAccuracy(double[] classification, double[] result){
		int i,correct=0,incorrect=0;
		for(i=0;i< classification.length;i++){
			if(classification[i]== result[i]){
				++correct;
			}else ++incorrect;
		}
		System.out.println((float)correct/(correct+incorrect)*100);
		System.out.println(correct+"//"+(correct+incorrect));
	}
	
	private int getMaximum(Mat classificationResult){
		 Core.MinMaxLocResult mmr = Core.minMaxLoc(classificationResult);
		 return (int)mmr.maxLoc.x+1;
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
	 
	 public static void main(String[] args){
		 NeuralNetwork nn = new NeuralNetwork();
	 }
}
