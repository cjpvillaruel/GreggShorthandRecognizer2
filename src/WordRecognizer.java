import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class WordRecognizer {
	Shorthand unknown;
	Mat image;
	ML ml;
	WordDB db;
	
	public WordRecognizer(){
		ml= new ML();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		db= new WordDB();
	}
	
	public void recognize(Shorthand word) throws IOException, ClassNotFoundException, SQLException{
		Mat featuresMat= this.getFeature(word.getImage());
		double [] results=ml.predict(featuresMat,1);
		word.annRes=db.getWord((int)results[0]);
		word.svmRes= db.getWord((int)results[1]);
		word.bnRes= db.getWord((int)results[2]);
		//System.out.println(results[0] +" "+results[1]+" "+ results[2]);
	}
	public Mat getFeature(Mat image){
		Mat featuresMat= new Mat(1,5, CvType.CV_32F);
		/**
		 * width and height of the contour (largest contour)
		 *  center of mass
		 *  moments
		 *  
		**/
		int width=0, height=0;
		double m00=0,m01=0, m10=0;
		String features= "";
		//Highgui.imwrite("images/res/resize.png", image);
		 // finding the contours
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_OTSU);
        Mat hierarchy = new Mat();
        Mat image2= image.clone();
        
        Imgproc.threshold(image, image, 220, 128, Imgproc.THRESH_BINARY_INV);
        Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        
       // finding best bounding rectangle for a contour whose distance is closer to the image center that other ones
        double d_min = Double.MAX_VALUE;
        Rect rect_min = new Rect();
        double area1=Double.MAX_VALUE;
        int i=0;
        MatOfPoint2f  approxCurve = new MatOfPoint2f();
        for (MatOfPoint contour : contours) { 	
        	
            MatOfPoint2f contour2f = new MatOfPoint2f( contour.toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
            
            area1 = Imgproc.contourArea(contour);
            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            //crop
        	Mat result = image2.submat(rect);
        	if(rect.width> width || rect.height > height){
        		width= rect.width;
            	height= rect.height;
        	}
        	Moments m= Imgproc.moments(result, true);
        	m00= m.get_m00();
        	m01 = m.get_m01();
        	m10= m.get_m10();
        	//System.out.println("moments 01="+m.get_m00() +" height:"+rect.height+" width: "+rect.width);
        	//Highgui.imwrite("images/res/result"+i+".png", result);
        	//create box
        	//Core.rectangle(image2,new Point(rect.x-5,rect.y-5),new Point(rect.x+rect.width+5,rect.y+rect.height+5), new Scalar(0, 255, 255),3);
        	i++;
          	
        }
        float x= m00!=0? (float)(m01/m00):0;
        float y= m00!=0? (float)(m10/m00):0;
//        features=width+","+height+","+x+","+y+","+area1+","+unknown.id;
        features+=" 1:"+width+" 2:"+height+" 3:"+x+" 4:"+y+" 5:"+area1;
        featuresMat.put(0,0,width );
        featuresMat.put(0,1,height );
        featuresMat.put(0,2,x );
        featuresMat.put(0,3,y );
        featuresMat.put(0,4,area1 );

        return featuresMat;
	}
	
}
