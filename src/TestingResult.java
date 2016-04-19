import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class TestingResult {
	String[] words;
	ArrayList<Shorthand> samples;
	double[] precisionANN, recallANN;
	double[] precisionSVM, recallSVM;
	double[] precisionBN, recallBN;
	Mat svmConf, annConf, bnConf;
	double overallPresicionANN,overallPresicionSVM, overallPresicionBN; 
	double overallRecallANN,overallRecallSVM, overallRecallBN; 
	double accuracyANN, accuracySVM, accuracyBN;
	public TestingResult(String[] words, ArrayList<Shorthand> samples){
		this.words= words;
		this.samples= samples;
		annConf= this.getConfusionMatrix("ann");
		svmConf= this.getConfusionMatrix("svm");
		bnConf= this.getConfusionMatrix("bn");
		overallPresicionANN= this.average(precisionANN);
		overallPresicionSVM= this.average(precisionSVM);
		overallPresicionBN= this.average(precisionBN);
	
		overallRecallANN= this.average(recallANN);
		overallRecallSVM= this.average(recallSVM);
		overallRecallBN= this.average(recallBN);
		
	}
	private double average(double[] data){
		double total=0;
		for(int i=0;i<data.length;i++)
			total+=data[i];
		
		return total/data.length;
	}
	private double computeAccuracy(Mat confusionMatrix){
		double totaltp=0;
		int totalSamples=0;
		for(int i=0;i<confusionMatrix.rows();i++){
			for(int j=0;j<confusionMatrix.cols();j++){
				totaltp += (int)confusionMatrix.get(i,i)[0];
				totalSamples = (int)confusionMatrix.get(i,j)[0];
			}
		}
		double accuracy = totaltp / totalSamples;
		return accuracy;
	}
	private double[] getPrecision(Mat confusionMatrix){
		double[] precisionArray= new double[words.length];
		for(int i=0;i<words.length;i++){
			double fp = 0;
			for(int j=0;j<words.length;j++){
				if(j != i)
				fp+=(int)confusionMatrix.get(j,i)[0];
			}
			int tp =(int)confusionMatrix.get(i,i)[0]; //true positive
			double precision =0;
			if(!(tp== 0 && fp ==0))
				precision= tp/(tp+fp)*100;
			precisionArray[i]= precision;
		}
		
		return precisionArray;
	}
	private double[] getRecall(Mat confusionMatrix){
		double[] recallArray= new double[words.length];
		double recall;
		for(int i=0;i<words.length;i++){
			//get false negative
			double fn=0;
			for(int j=0;j<words.length;j++){
				if(i != j)
					fn+=(int)confusionMatrix.get(i,j)[0];
			}
			int tp =(int)confusionMatrix.get(i,i)[0]; //true positive
			recall= tp/(tp+fn)*100;
			recallArray[i]= recall;
		}
		return recallArray;
	}
	private void convertToPercentage(Mat confusionMatrix){
		for(int i=0;i<words.length;i++){
			//get total sample per word
			double wordTotal=0;
			for(int j=0;j<words.length;j++){
				wordTotal+=(int)confusionMatrix.get(i,j)[0];
			}
//			//get percentage for each value
//			for(int j=0;j<words.length;j++){
//				double tp =confusionMatrix.get(i,j)[0]; 
//				double precision= tp/wordTotal*100;
//				confusionMatrix.put(i, j, precision);
//			}
		}
		
	}
	
	private Mat getConfusionMatrix(String ml){
		Mat confusionMatrix= Mat.zeros(words.length, words.length,CvType.CV_32F);
		
		for(int i=0;i<samples.size();i++){
			//get actual class
			String actual = samples.get(i).word;
			int actualIndex = Arrays.asList(words).indexOf(actual);
			//get predicted class
			
			String predicted="";
			switch(ml){
				case "ann": predicted= samples.get(i).annRes; break;
				case "svm": predicted= samples.get(i).svmRes; break;
				case "bn":  predicted= samples.get(i).bnRes;  break;
			}
			int predictedIndex = Arrays.asList(words).indexOf(predicted);
			
			//increment value of confusionMatrix[actual, predicted]
			int val = (int)confusionMatrix.get(actualIndex, predictedIndex)[0]+1;
			confusionMatrix.put(actualIndex, predictedIndex, val);
		}
		double total=0;
		
		System.out.println(confusionMatrix.dump());
		double accuracy = this.computeAccuracy(confusionMatrix);
		this.convertToPercentage(confusionMatrix);
		//get precision and recall
		double[] pre=this.getPrecision(confusionMatrix);
		double[] rec=this.getRecall(confusionMatrix);
		switch(ml){
			case "ann": this.precisionANN = pre;
						this.recallANN =rec;
						this.accuracyANN = accuracy;
						break;
			case "svm": this.precisionSVM = pre;
						this.recallSVM =rec;
						this.accuracySVM = accuracy;
						break;
			case "bn":  this.precisionBN = pre;
						this.recallBN =rec;
						this.accuracyBN = accuracy;
						break;
		}
		
		return confusionMatrix;
	}
	
	public Mat getConfusinMatrix(String ml){
		Mat conf = null;
		switch(ml){
			case	"ann": conf= annConf; break;
			case	"svm": conf= svmConf; break;
			case	"bn": conf= bnConf; break;
		}
		return conf;
	}
}
