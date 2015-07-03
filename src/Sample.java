import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


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
      
      Sample n= new Sample();
      n.templateMatching();
   }
}