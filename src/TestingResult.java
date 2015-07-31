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
	public TestingResult(String[] words, ArrayList<Shorthand> samples){
		this.words= words;
		this.samples= samples;
		this.getConfusionMatrix("ann");
		this.getConfusionMatrix("svm");
		this.getConfusionMatrix("bn");
		
	}
	
	private double[] getPrecision(Mat confusionMatrix){
		double[] precisionArray= new double[words.length];
		for(int i=0;i<words.length;i++){
			//get total sample per word
			double wordTotal=0;
			for(int j=0;j<words.length;j++){
				wordTotal+=(int)confusionMatrix.get(i,j)[0];
			}
			int tp =(int)confusionMatrix.get(i,i)[0]; //true positive
			double precision= tp/wordTotal*100;
			precisionArray[i]= precision;
		}
		
		return precisionArray;
	}
	private double[] getRecall(Mat confusionMatrix){
		double[] recallArray= new double[words.length];
		for(int i=0;i<words.length;i++){
			//get total sample per word
			double wordTotal=0;
			for(int j=0;j<words.length;j++){
				wordTotal+=(int)confusionMatrix.get(j,i)[0];
			}
			int tp =(int)confusionMatrix.get(i,i)[0]; //true positive
			double recall= tp/wordTotal*100;
			recallArray[i]= recall;
		}
		
		return recallArray;
	}
	private void getConfusionMatrix(String ml){
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
		//get precision and recall
		double[] pre=this.getPrecision(confusionMatrix);
		double[] rec=this.getRecall(confusionMatrix);
		switch(ml){
			case "ann": this.precisionANN = pre;
						this.recallANN =rec;
						break;
			case "svm": this.precisionSVM = pre;
						this.recallSVM =rec;
						break;
			case "bn":  this.precisionBN = pre;
						this.recallBN =rec;
						break;
		}
		
		
	}
}
