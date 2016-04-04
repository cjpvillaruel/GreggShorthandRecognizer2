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

public class WordRecognizer {
	private static final int ATTRIBUTES = 13;
	Shorthand unknown;
	Mat image;
	ML ml;
	WordDB db;
	FeatureExtraction fextract;
	
	public WordRecognizer(){
		ml= new ML();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		db= new WordDB();
		fextract = new FeatureExtraction();
	}
	
	public void recognize(Shorthand word) throws IOException, ClassNotFoundException, SQLException{
		Mat featuresMat= fextract.getFeatures(word.getImage());
		double [] results=ml.predict(featuresMat,1);
		word.annRes=db.getWord((int)results[0]);
		word.svmRes= db.getWord((int)results[1]);
		word.bnRes= db.getWord((int)results[2]);
		
	}
	
}
