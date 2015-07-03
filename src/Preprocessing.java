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
public class Preprocessing {
	private static final int HEADERMARGIN = 95;
	private static final int LEFTMARGIN = 215;
	private static final int BOXHEIGHT= 80;
	private static final int BOXWIDTH= 130;
	//private static final int CLASSES= 10;
	private WordDB db;
	private int numClasses;
	public Preprocessing(){
		db= new WordDB();
	}
	
	public Preprocessing(int numClasses){
		this.numClasses= numClasses;
		db= new WordDB();
	}

	
	public void removeBorder(Mat character){
		int x,y;
		int margin=5;
		int height= character.height();
		int width= character.width();
		for(y=0; y<width; y++){
			for(x=0;x<width;x++){
				if(x < margin || x > height-margin || y<margin || y>width-margin){
					//clean
					character.put(x,y,255.0);

				}
			}
		}
	}
	public void convertToBits(String folderpath, String filename){
		
		String test="";
		int offset=0;
		//get words from db
		String sql = "SELECT word, id FROM word LIMIT 30 OFFSET "+offset; 
		String words[]= new String[30];
		int classes[]= new int[30];
		int index=0;
        try {
			ResultSet rs= db.select(sql);
			while(rs.next()){
				words[index]=rs.getString("word");
				classes[index]= rs.getInt("id");
				index++;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
		
		for(int j = 0 ; j < words.length ; j++){
			
			int files = new File(folderpath +words[j]).listFiles().length;
			for(int k=1; k <= files; k++){
				Mat letter = Highgui.imread(folderpath +words[j]+"/"+"word ("+k+").jpg",Highgui.IMREAD_GRAYSCALE);
				
            	Mat resizeimage = new Mat();
            	Size sz = new Size(50,25);
            	resizeimage= letter.clone();
            	Imgproc.resize( letter, resizeimage, sz );
            	//Highgui.imwrite("images/letters/"+letters[j]+"/"+"resize"+k+".png", resizeimage);
				for(int x =0; x< resizeimage.height(); x++){
					for(int y=0; y< resizeimage.width(); y++){
						//System.out.println(letter.get(x,y)[0]);
						if(resizeimage.get(x, y)[0] > 220.0)
						test+="0";
						else{ test+="1";
						
						}
						test+=",";
					}
				}
				test+=classes[j]+"\n";
			}
			
		}
	
		//creating a file
		try{
			File file = new File(filename);
			 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
				
			}
	
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(test);
			bw.close();
			System.out.print("File updated");
		}
		catch(Exception e){
			
		}
		
	}

	   public Point templateMatching(Mat img){
		   System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		   int match_method=5;
		   int max_Trackbar = 5;
		   Mat temp = Highgui.imread("images/template2.jpg");
		   
		   
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
		   Mat s= img.clone();
	       Core.rectangle(s, matchLoc, new Point(matchLoc.x + temp.cols(),
	               matchLoc.y + temp.rows()), new Scalar(0, 255, 0));

	       // Save the visualized detection.
	      Highgui.imwrite("images/samps.jpg", s);
	       return matchLoc;
	   }

	public void cropImages(int num, String pathname, String destinationPath){
		String path= pathname+num;
		int count=0;
		int fileNumber = new File(path).listFiles().length;
		int offset;
		if(num==1){
			offset=0;
		}
		else{
			offset= (num-1)*10;
		}
		//get words from db
		String sql = "SELECT word FROM word LIMIT 10 OFFSET "+offset; 
		String words[]= new String[10];
		int index=0;
        try {
			ResultSet rs= db.select(sql);
			while(rs.next()){
				words[index]=rs.getString("word");
				index++;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
       
		for(int i=1 ; i <= fileNumber; i++){
			int wordCount=0;
			Mat data= Highgui.imread(path+"/data ("+i+").jpg",Highgui.IMREAD_GRAYSCALE);
			Mat data1= Highgui.imread(path+"/data ("+i+").jpg");
			Mat destination= data.clone();
	        Imgproc.threshold(data,destination,210,255,Imgproc.THRESH_BINARY);
	        Point margin= templateMatching(data1);
	        index=0;
	        int marginY = (int)margin.y, marginX= (int)margin.x+49+BOXWIDTH;
	        if(margin.y > HEADERMARGIN ){
	        	marginY= HEADERMARGIN;
	        }
	        else if(margin.x > LEFTMARGIN){
	        	marginX= LEFTMARGIN;
	        	System.out.println("lef");
	        }
	        else marginY=(int)margin.y+55; 
			for(int y=(int)marginY; y< data.height()-280; y+=BOXHEIGHT+5){
				for(int x=(int)marginX; x< data.width()-200;x+=BOXWIDTH+5){
					Rect letter= new Rect(x, y, BOXWIDTH, BOXHEIGHT);
	        		Mat result = data.submat(letter);
	        		removeBorder(result);
	        		try{
		        		wordCount = new File(destinationPath+words[index]).listFiles().length+1;
		        		//System.out.println(words[index]); 
		            	}catch(Exception e){
		            		boolean success = (new File(destinationPath+words[index])).mkdirs();
		            		wordCount=1;
		            	}
		        		Highgui.imwrite(destinationPath+words[index]+"/word ("+wordCount+")"+".jpg", result);
	        		count++;
				}
				index++;
			}
		}
	}
	public String getFeatures(String path, int index){
		//read file
		
		//get feature:
		/**
		 * width and height of the contour (largest contour)
		 *  center of mass
		 *  moments
		 *  
		**/
		int width=0, height=0;
		double m00=0,m01=0, m10=0;
		
		Mat image= Highgui.imread(path,Highgui.IMREAD_GRAYSCALE);
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
        int i=0;
        MatOfPoint2f  approxCurve = new MatOfPoint2f();
        for (MatOfPoint contour : contours) { 	
        	
            MatOfPoint2f contour2f = new MatOfPoint2f( contour.toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
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
        features=width+","+height+","+x+","+y+","+index;
        features+="="+index+" 1:"+width+" 2:"+height+" 3:"+m00+" 4:"+m01;
        //System.out.println(features);
        return features;
	}
	
	public void convert(){
		Mat letter = Highgui.imread("images/words/acre/"+"word ("+1+").jpg",Highgui.IMREAD_GRAYSCALE);
		String test="";
    	Mat resizeimage = new Mat();
    	Size sz = new Size(65,40);
    	//resizeimage= letter.clone();
    	Imgproc.resize( letter, resizeimage, sz );
    	//Highgui.imwrite("images/letters/"+letters[j]+"/"+"resize"+k+".png", resizeimage);
		for(int x =0; x< resizeimage.height(); x++){
			for(int y=0; y< resizeimage.width(); y++){
				//System.out.println(letter.get(x,y)[0]);
				if(resizeimage.get(x, y)[0] > 220.0)
				test+="0";
				else{ test+="1";
				System.out.println(resizeimage.get(x, y)[0]+" "+x+" "+y);
				}
				test+=",";
			}
		}
		test+=1+"\n";
	}
	
	public int getAllFeatures(String folderpath,String filename){
		
		String test="";
		int offset=0;
		//get words from db
		String sql = "SELECT word, id FROM word LIMIT "+numClasses+ " OFFSET "+offset; 
		String words[]= new String[numClasses];
		int classes[]= new int[numClasses];
		int index=0;
        try {
			ResultSet rs= db.select(sql);
			while(rs.next()){
				words[index]=rs.getString("word");
				classes[index]= rs.getInt("id");
				index++;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
		int number=0;
		String data2="";
		for(int j = 0 ; j < words.length ; j++){
			System.out.println(j);
			int files = new File(folderpath +words[j]).listFiles().length;
			for(int k=1; k <= files; k++){
				
				String[] str= this.getFeatures(folderpath +words[j]+"/"+"word ("+k+").jpg", classes[j]).split("=");
				test+=str[0]+"\n";
				data2+=str[1]+"\n";
				number++;
			}
			
		}
	
		//creating a file
		try{
			File file = new File(filename+".txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(test);
			bw.close();
			System.out.print("File updated");
		}
		catch(Exception e){	
		}
		
		//create file for svm
		try{
			
			File file = new File(filename+"_svm.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data2);
			bw.close();
		}
		catch(Exception e){	
		}
		
		return number;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Read an image.
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Preprocessing p= new Preprocessing();
		//p.cropImages(1,"images/training_data/", "images/training_words/");
		
//		Mat a= new Mat(2,2, CvType.CV_32FC1);
//		//p.convert();
		
//		ML ml= new ML();
//		ml.bayes();
		//ml.svm();
		//p.getFeatures("images/words/ache/word (1).jpg", 1);
		//p.getFeatures("images/erosion.jpg", "word");
		//p.getAllFeatures("images/training_words/", "training_data.txt");
		//p.getAllFeatures("images/testing_words/", "testing_data.txt");
		
		
		
	
	}

}
