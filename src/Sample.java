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
   public static void main( String[] args )
   {
      System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
      
//      Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1 );
//      System.out.println( "mat = " + mat.dump() );
      
   //   Sample n= new Sample();
   //   n.templateMatching();
      
      //put text in image
//      Mat data= Highgui.imread("images/erosion.jpg");
      
//      Core.putText(data, "Sample", new Point(50,80), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0,0,0),2); 
//   
//      Highgui.imwrite("images/erosion2.jpg", data);
      
      //getting dct of an image
      String path = "images/croppedfeature/in (2).jpg";
      
      Mat image= Highgui.imread(path,Highgui.IMREAD_GRAYSCALE);
      ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
      Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_OTSU);
      Mat hierarchy = new Mat();
      Imgproc.threshold(image, image, 220, 128, Imgproc.THRESH_BINARY_INV);
      Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
     // System.out.println(contours.get(0).dump());
      
      //get the freeman chain code from the contours
      int rows= contours.get(0).rows();
      Mat chainHistogram =Mat.zeros(1, 8, CvType.CV_32S);
      int direction = 7;
      Mat prevPoint = contours.get(0).row(0);
      System.out.println(prevPoint.dump());
      Mat contour = contours.get(0);
      for(int i=1;i<rows;i++){
    	 
    	//  System.out.print(contour.row(i).dump());
    	
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
      System.out.println("\n"+chainHistogram.dump());
   }
}