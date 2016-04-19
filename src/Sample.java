import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;



import java.util.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


//import com.sun.speech.freetts.en.us.CMULexicon;
public class Sample
{
	
   public void templateMatching(){
	   System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	   int match_method=5;
	   int max_Trackbar = 5;
	   Mat data= Highgui.imread("images/training_data/1"+"/data ("+1+").jpg");
	   Mat temp = Highgui.imread("images/template.jpg");
	   Mat img= data.clone();
	   
	   int result_cols =  img.cols() - temp.cols() + 1;
	   int result_rows = img.rows() - temp.rows() + 1;
	   Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
	  
	   Imgproc.matchTemplate( img, temp, result,match_method );
	   Core.normalize( result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat() );
	   
	   double minVal; double maxVal;
	   Point minLoc; Point maxLoc;
	   Point matchLoc;
	   //minMaxLoc( result, &minVal, &maxVal, &minLoc, &maxLoc, Mat() );
	   Core.MinMaxLocResult res= Core.minMaxLoc(result);
	   
	   
	   if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
           matchLoc = res.minLoc;
       } else {
           matchLoc =res.maxLoc;
       }
	   
	   // / Show me what you got
       Core.rectangle(img, matchLoc, new Point(matchLoc.x + temp.cols(),
               matchLoc.y + temp.rows()), new Scalar(0, 255, 0));

       // Save the visualized detection.
       Highgui.imwrite("images/samp.jpg", img);
   }
   
   public void copyMat(Mat src, Mat dest){
	   int srcRows= src.rows();
	   int srcCols= src.cols();
	   int destRows= dest.rows();
	   int destCols= dest.cols();
	   
	   for(int i=0;i<srcRows;i++){
		   for(int j=0;j< srcCols;j++){
			   double bit= src.get(i,j)[0];
			   dest.put(i, j, bit);
			   System.out.println(bit);
		   }
	   }
   }
	public static Mat getCCH(Mat image){
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
		    	System.out.print(direction);
		    	
		      }
		    
		}
		 System.out.println("\n"+chainHistogram.dump());
		Scalar alpha = new Scalar(n); // the factor
	    Core.divide(chainHistogram,alpha,chainHistogram);
	    System.out.println("\nrows="+n+" "+chainHistogram.dump());
		return chainHistogram;
	}
	
   public static void main( String[] args ){
      System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
      
//      Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1 );
//      System.out.println( "mat = " + mat.dump() );
      
     Sample n= new Sample();
   //   n.templateMatching();
      
      //put text in image
//      Mat data= Highgui.imread("images/erosion.jpg");
      
//      Core.putText(data, "Sample", new Point(50,80), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0,0,0),2); 
//   
//      Highgui.imwrite("images/erosion2.jpg", data);
      
      //getting dct of an image
      String path = "images/croppedfeature/go (20).jpg";
      path = "images/wordseg/img1.png";
      Mat image= Highgui.imread(path,Highgui.IMREAD_GRAYSCALE);
      ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
      
      Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_OTSU);
      Imgproc.threshold(image, image, 220, 128, Imgproc.THRESH_BINARY_INV);
      Mat newImg = new Mat(45,100, image.type());
      
      newImg.setTo(new Scalar(0));
      n.copyMat(image, newImg);
     
      int vgap= 25;
      int hgap= 45/3;
      
      Moments m= Imgproc.moments(image, false);
      Mat hu= new Mat();
      Imgproc.HuMoments(m, hu); 
      System.out.println(hu.dump());
      
//      //divide the mat into 12 parts then get the features of each part
//      int count=1;
//      for(int j=0; j<45; j+=hgap){
//    	  for(int i=0;i<100;i+=vgap){
//    		  Mat result = newImg.submat(j, j+hgap, i, i+vgap);
//    		
//    		  
//    		  Moments m= Imgproc.moments(result, false);
//    		  double m01= m.get_m01();
//    		  double m00= m.get_m00();
//    		  double m10 = m.get_m10();
//    		  int x= m00!=0? (int)(m10/m00):0;
//    		  int y= m00!=0? (int)(m01/m00):0;
//    		  Mat hu= new Mat();
//    		  Imgproc.HuMoments(m, hu); 
//    		  System.out.println(hu.dump());
//    		  System.out.println(count+" :"+x+" and "+y);
//    		  Imgproc.threshold(result, result, 0,254, Imgproc.THRESH_BINARY_INV);
//    		  Highgui.imwrite("images/submat/"+count+".jpg", result);
//    		  count++;
//    		  
//    	  }
//      }
// 
//    for(int i=vgap;i<100;i+=vgap){
//	  Point pt1= new Point(i, 0);
//      Point pt2= new Point(i, 99);
//      Core.line(newImg, pt1, pt2, new Scalar(0,0,0));
//  }
//  for(int i=hgap;i<45;i+=hgap){
//	  Point pt1= new Point(0, i);
//      Point pt2= new Point(99, i);
//      Core.line(newImg, pt1, pt2, new Scalar(0,0,0));
//  }
//      Highgui.imwrite("images/submat/copyto.jpg", newImg);
   }
   
}