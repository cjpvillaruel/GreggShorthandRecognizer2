import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class WordRecognizer implements Constants {
	Shorthand unknown;
	Mat image;
	ML ml;
	WordDB db;
	FeatureExtraction fextract;
	double[] mean, stdDev;
	public WordRecognizer(){
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		ml= new ML();
		db= new WordDB();
		fextract = new FeatureExtraction();
		mean= new double[ATTRIBUTES];
		stdDev= new double[ATTRIBUTES];
	}
	
	public void recognize(Shorthand word) throws Exception{
		Mat featuresMat= fextract.getFeatures(word.getImage());
		double [] results=ml.predict(featuresMat,1);
		word.annRes=db.getWord((int)results[0]);
		word.svmRes= db.getWord((int)results[1]);
		word.bnRes= db.getWord((int)results[2]);
		
	}
	public void normalize(Mat featureMat){
		for(int i=0;i< ATTRIBUTES;i++){
			double val = featureMat.get(0, i)[0];
			double norm = (val- mean[i])/stdDev[i];
			featureMat.put(0, i,norm );
		}
	}
	public void readScale(){
		int i=0;
		String  line = null;
		try{
			// open input stream test.txt for reading purpose.
			FileReader fl = new FileReader("scale.txt");
			BufferedReader br = new BufferedReader(fl);
			while(true){
				line = br.readLine();
				if(line== null)break;
				String[] data = line.split(",");
				mean[i]= Double.parseDouble(data[0]);
				stdDev[i]= Double.parseDouble(data[1]);
				i++;
			}
			br.close();
		  }catch(Exception ex){
		     ex.printStackTrace();
		  }
	}
	
	public static void main(String[] arg){
		WordRecognizer r= new WordRecognizer();
		r.readScale();
		System.out.println(r.stdDev[1]);
	}
}
