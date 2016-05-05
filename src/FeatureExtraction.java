import java.util.ArrayList;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
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

public class FeatureExtraction implements Constants {

	private int index;
	private Mat image;
	public Mat featuresMat;

	public FeatureExtraction(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	/**
	 * width and height of the contour (largest contour)
	 *  center of mass
	 *  moments
	 *  chain code histogram
	 *  
	**/	
	private Mat computeFeatures(Mat image){
		
		this.featuresMat= new Mat(1,ATTRIBUTES, CvType.CV_32F);
		//get feature:
		
		int width=0, height=0;
		double m00=0,m01=0, m10=0;
		Mat hu= new Mat();
		// finding the contours
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_OTSU);
		Mat hierarchy = new Mat();
		Mat image2= image.clone();
        
		Imgproc.threshold(image, image, 220, 128, Imgproc.THRESH_BINARY_INV);
		Mat chaincode= getCCH(image);
		Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		// finding best bounding rectangle for a contour whose distance is closer to the image center that other ones
		double d_min = Double.MAX_VALUE;
		Rect rect_min = new Rect();
		double area1=Double.MAX_VALUE;
		int i=0;
		MatOfPoint2f  approxCurve = new MatOfPoint2f();
        

		if(contours.size()> 1){
        	//get the points of each contours
			double x[]= new double[contours.size()];
			double y[]= new double[contours.size()];
			
			int maxX=0,maxY=0; 
			int minX=Integer.MAX_VALUE,minY=Integer.MAX_VALUE;
        	
        	
			for (MatOfPoint contour : contours) { 		
				MatOfPoint2f contour2f = new MatOfPoint2f( contour.toArray() );
				//Processing on mMOP2f1 which is in type MatOfPoint2f
				double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
				Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
				//Convert back to MatOfPoint
				MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
				// Get bounding rect of contour
				Rect rect = Imgproc.boundingRect(points);
                if(rect.x+ rect.width > maxX){
                	maxX= rect.x + rect.width;
                }
                if(rect.x < minX){
                	minX= rect.x;
                }
                if(rect.y+rect.height > maxY){
                	maxY= rect.y+rect.height;
                }
                if(rect.y < minY){
                	minY= rect.y;
                }
        	}
			Mat result = image.submat(minY, maxY, minX, maxX);
			width= result.cols();
			height= result.rows();
			Moments m= Imgproc.moments(result, true);
			m00= m.get_m00();
			m01 = m.get_m01();
			m10= m.get_m10();
			area1 = m00;
			Imgproc.HuMoments(m, hu); 
        }
        else{
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
	        	Mat result = image.submat(rect);
	        	if(rect.width> width || rect.height > height){
	        		
	        		width= rect.width;
	            	height= rect.height;
	        	}
	        	Moments m= Imgproc.moments(result, true);
	        	m00= m.get_m00();
	        	m01 = m.get_m01();
	        	m10= m.get_m10();
	        	//area1 = m00;
	        	Imgproc.HuMoments(m, hu);
	        	//Highgui.imwrite("images/croppedfeature/"+newfilename, result);
	        	//System.out.println("moments 01="+m.get_m00() +" height:"+rect.height+" width: "+rect.width);
	        	//Highgui.imwrite("images/res/result"+i+".png", result);
	        	//create box
	        	//Core.rectangle(image2,new Point(rect.x-5,rect.y-5),new Point(rect.x+rect.width+5,rect.y+rect.height+5), new Scalar(0, 255, 255),3);
	        	i++;
	          	
	        }
        }
        
        float x= m00!=0? (float)(m01/m00):0;
        float y= m00!=0? (float)(m10/m00):0;

        featuresMat.put(0,0,width );
        featuresMat.put(0,1,height );
        featuresMat.put(0,2,x );
        featuresMat.put(0,3,y );
        featuresMat.put(0,4,area1 );

        for(i=0;i<8;i++){
        	double hist=chaincode.get(0, i)[0];
        	hist= Math.round(hist * 100.0) / 100.0;
        	featuresMat.put(0,5+i,hist );
        }
        if(hu.rows()==0){
        	 Moments m= Imgproc.moments(image, false);
        	 hu= new Mat();
             Imgproc.HuMoments(m, hu); 
             
        }
        for(i=0;i<7;i++){
        	double huMoment=hu.get(i, 0)[0];
        	huMoment= Math.round(huMoment * 100.0) / 100.0;
        	featuresMat.put(0,13+i,huMoment );
        }
        int size= (int) featuresMat.total() * featuresMat.channels();
        double[] temp = new double[size];
        double[] a= new double[ATTRIBUTES];
        temp=featuresMat.get(0,3);
        this.featuresMat= featuresMat;
        return featuresMat;
	}
	/**
	 * computes the chain code histogram of an image
	 * 
	 * @param image  Mat
	 * @return chainHistogram
	 */
	public Mat getCCH(Mat image){
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);	
		Mat chainHistogram =Mat.zeros(1, 8, CvType.CV_32F);
		int n=0;
		MatOfPoint2f  approxCurve = new MatOfPoint2f();
		for(MatOfPoint contour:contours){  
			//get the freeman chain code from the contours
			int rows= contour.rows();
			//System.out.println("\nrows"+rows+"\n"+contour.dump());
		    int direction = 7;
		    Mat prevPoint = contours.get(0).row(0);
		    n+=rows-1;
		    for(int i=1;i<rows;i++){
		    	//get the current point
		    	double x1 =  contour.get(i-1,0)[1];
		    	double y1 =  contour.get(i-1, 0)[0];
		    	  
		    	//get the second point
		    	double x2 =  contour.get(i,0)[1];
		    	double y2 =  contour.get(i,0)[0];
		    	      	  
		    	if(x2==x1 && y2 == y1+1)
		    		  direction =0;
		    	else if(x2 == x1-1 && y2 == y1+1)
		    		  direction =1;
		    	else if(x2 == x1-1 && y2 == y1)
			    	  direction =2;
		    	else if(x2 == x1-1 && y2 == y1-1)
			    	  direction =3;
		    	else if(x2 == x1 && y2 == y1-1 )
			    	  direction =4;
		    	else if(x2 == x1+1 && y2 == y1-1)
			    	  direction =5;
		    	else if(x2 == x1+1 && y2 == y1)
			    	  direction =6;
		    	else if(x2== x1+1 && y2== y1+1)
			    	  direction =7;
		    	else
		    		  System.out.print("err");
		    	double counter = chainHistogram.get(0, direction)[0];
		    	chainHistogram.put(0, direction, ++counter);
		    	
		      }
		}
		 //System.out.println("\n"+chainHistogram.dump());
		Scalar alpha = new Scalar(n); // the factor
	    Core.divide(chainHistogram,alpha,chainHistogram);
	    //System.out.println("\nrows="+n+" "+chainHistogram.dump());
		return chainHistogram;
	}
	/**
	 * formats the features for the file write of the training and testing data
	 * 
	 * @param featuresMat
	 * @param index
	 * @return featureString
	 */
	private String formatFeatures(Mat featuresMat, int index){
		String featureString="";
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		String svmFormat=index+" ";
		for(int i=0;i<ATTRIBUTES;i++){
			double feature= featuresMat.get(0, i)[0];
			svmFormat+=(i+1)+":"+feature+" ";
			featureString+=feature+",";
		}
		featureString+= index+"="+svmFormat;

		return featureString;
	}
	/**
	 * returns a Mat that contains all of the features
	 * 
	 * @param image
	 * @return features
	 */
	public Mat getFeatures(Mat image){
		Mat features= computeFeatures(image);
		return features;
	}
	/**
	 * computes the features of an image given a path and an index for the label of the image
	 * 
	 * @param path
	 * @param index
	 * @return
	 */
	public String getFeatures(String path, int index){
		Mat image= Highgui.imread(path,Highgui.IMREAD_GRAYSCALE);
		Mat features= this.computeFeatures(image);
		this.featuresMat= features;
		return formatFeatures(features, index);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FeatureExtraction fe= new FeatureExtraction();
		String a= fe.getFeatures("images/training_words/a/word (1).jpg", 1);
		//System.out.println(a);
	}

}
