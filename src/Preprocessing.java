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
	private FeatureExtraction fextract;
	private int numClasses;
	public Preprocessing(){
		db= new WordDB();
		fextract = new FeatureExtraction();
	}
	
	public Preprocessing(int numClasses){
		this.numClasses= numClasses;
		db= new WordDB();
		fextract = new FeatureExtraction();
	}

	
	public void removeBorder(Mat character){
		int x,y;
		int margin=10;
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
	
	/**
	 * 
	 * @param num		int      - dataset number
	 * @param pathname  String   - source folder eg "images/training_data/" 
	 * @param destinationPath String - destination folder eg "images/training_words/"
	 */
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
		        		Highgui.imwrite(destinationPath+words[index]+"/"+words[index]+" ("+wordCount+")"+".jpg", result);
	        		count++;
				}
				index++;
			}
		}
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
	/**
	 * Perform thinning on all images in a training/testing folder.
	 * 
	 *
	 * @param  folderpath    e.g. images/training_words
	 * 
	 */
	public void thinAllWords(String folderpath){
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
			System.out.println(words[j]);
			//create folder
			
            boolean success = (new File(folderpath+"_thinned/"+words[j])).mkdirs();
            
			int files = new File(folderpath +"/"+words[j]).listFiles().length;
			for(int k=1; k <= files; k++){
				thinning(folderpath +"/"+words[j]+"/"+"word ("+k+").jpg");
			}
			
		}
	}
	
	/**
	 * Perform one thinning on an image
	 * reference:
	 *  http://opencv-code.com/quick-tips/implementation-of-guo-hall-thinning-algorithm/
	 *
	 * @param  path        e.g. images/training_words/a/word (1).jpg
	 * 
	 */
	public void thinning(String path){
		//read image
		Mat word = Highgui.imread(path,Highgui.IMREAD_GRAYSCALE);
		Mat bw= new Mat();
		Imgproc.threshold(word, word, 200, 255, Imgproc.THRESH_BINARY);
		//dilate
		Mat structElement = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3,3));
		Imgproc.erode(word, word, structElement);
		
		//invert image 
		Core.bitwise_not(word,word);
//		Highgui.imwrite("images/thinned/orig.jpg",word);

		Mat diff= new Mat();
		Mat prev= Mat.zeros(word.size(), CvType.CV_8UC1);
		int countSimilar=0, nonZeroCount=Core.countNonZero(diff);
		
		do{
			Core.divide(255, word, word);
			word=thinningGuoHallIteration(word, 0);
			word=thinningGuoHallIteration(word, 1);
			Core.absdiff(word, prev, diff);
			word.copyTo(prev);
			if(nonZeroCount == Core.countNonZero(diff))
				countSimilar++;
			else{
				countSimilar=0;
				nonZeroCount=Core.countNonZero(diff);
			}
		}while(countSimilar!= 10);
		
		Mat s= new Mat(word.size(),CvType.CV_8UC1,new Scalar(255));
		
		word= word.mul(s);
		prev= prev.mul(s);
		Core.bitwise_not(word,word);
		String destpath= path.replace("words", "words_thinned");
		Highgui.imwrite(destpath,word);
	}
	
	/**
	 * Perform one thinning iteration.
	 * reference:
	 *  http://opencv-code.com/quick-tips/implementation-of-guo-hall-thinning-algorithm/
	 *
	 * @param  img    Binary image with range = 0-1
	 * @param  iter  0=even, 1=odd
	 */
	public Mat thinningGuoHallIteration(Mat img, int iter){	
		Mat marker = Mat.zeros(img.size(), CvType.CV_8UC1);
		for(int i=1;i< img.rows()-1;i++){
			for(int j=1;j<img.cols()-1;j++){
				int p2 = (int)img.get(i-1, j)[0];
				int p3 = (int)img.get(i-1, j+1)[0];
				int p4 = (int)img.get(i, j+1)[0];
				int p5 = (int)img.get(i+1, j+1)[0];
				int p6 = (int)img.get(i+1, j)[0];
				int p7 = (int)img.get(i+1, j-1)[0];
				int p8 = (int)img.get(i, j-1)[0]; 
				int p9 = (int)img.get(i-1, j-1)[0];
				
				int C  = (~p2 & (p3 | p4)) + (~p4 & (p5 | p6)) +
	                     (~p6 & (p7 | p8)) + (~p8 & (p9 | p2));
	            int N1 = (p9 | p2) + (p3 | p4) + (p5 | p6) + (p7 | p8);
	            int N2 = (p2 | p3) + (p4 | p5) + (p6 | p7) + (p8 | p9);
	            int N  = N1 < N2 ? N1 : N2;
	            int m  = iter == 0 ? ((p6 | p7 | ~p9) & p8) : ((p2 | p3 | ~p5) & p4);
	            if (C == 1 && (N >= 2 && N <= 3) & m == 0)
	                marker.put(i, j, 1);
			}
		}	
		Core.bitwise_not(marker, marker);
		Core.bitwise_and(img,marker, img);
		return img;
	}
	
	public void saveFile(String filename, String text){
		try{
			File file = new File(filename+".txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
			System.out.print("File updated");
		}
		catch(Exception e){	
		}
	}
	public int getAllFeatures2(String folderpath,String filename){
		int totalSamples=0;
		//for each word folder inside the selected folderpath,
		//get the index then iterate on each images to get the features
		String test="",data2="";
		File[] files = new File(folderpath).listFiles();
		for (File file : files) {
			 if (file.isDirectory()) {
				String word= file.getName();
				 //find the index in the db
				try {
					int index= db.getIndex(word);
					File[] wordFolder= file.listFiles();
					for (File wordImage : wordFolder) {
						//get features of an image
						String[] str= fextract.getFeatures(wordImage.getAbsolutePath(), index).split("=");
						totalSamples++;
						test+=str[0]+"\n";
						data2+=str[1]+"\n";
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}//end for the trycatch
			}//endif
		}//endfor

		 saveFile(filename, test);
		 saveFile(filename+"_svm", data2);
		return totalSamples;	
	}
	public int getAllFeatures(String folderpath,String filename){

		String test="";
		int offset=0;
		//get words from db
		String sql = "SELECT word, id FROM word LIMIT "+numClasses+ " OFFSET "+offset; 
		String words[]= new String[numClasses];
		int classes[]= new int[numClasses];
		int index=0;
		System.out.println(numClasses);
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
			int files = new File(folderpath+"\\" +words[j]).listFiles().length;
			for(int k=1; k <= files; k++){
				
				String[] str= fextract.getFeatures(folderpath+"\\" +words[j]+"\\"+"word ("+k+").jpg", classes[j]).split("=");
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
		
		/**
		 * cropping all dataset
		 * 
		p.cropImages(1,"images/training_data/", "images/training_words/");
		p.cropImages(2,"images/training_data/", "images/training_words/");
		p.cropImages(3,"images/training_data/", "images/training_words/");
		
		p.cropImages(1,"images/testing_data/", "images/testing_words/");
		p.cropImages(2,"images/testing_data/", "images/testing_words/");
		p.cropImages(3,"images/testing_data/", "images/testing_words/");
		
		**/
//		p.numClasses=30;
//		p.thinAllWords("images/training_words");
//		p.thinAllWords("images/testing_words");
//		Mat a= new Mat(2,2, CvType.CV_32FC1);
//		//p.convert();
		
		//ML ml= new ML(2400,920 , 30);
		
//		ml.bayes();
		//ml.svm();
		//p.getFeatures("images/words/ache/word (1).jpg", 1);
		//p.getFeatures("images/erosion.jpg", "word");
		//p.getAllFeatures("images/training_words/", "training_data.txt");
		//p.getAllFeatures("images/testing_words/", "testing_data.txt");
		//p.thinning();
		
		
//		Mat word = Highgui.imread("images/training_words/are/word (1).jpg",Highgui.IMREAD_GRAYSCALE);
//		Mat bw= new Mat();
//		Imgproc.threshold(word, word, 200, 255, Imgproc.THRESH_BINARY);
		//dilate
//		Mat structElement = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3,3));
//		Imgproc.erode(word, word, structElement);
//		Highgui.imwrite("images/thinned/dilate.jpg",word);
		String path= "images\\training_words";
		String path2= "images\\testing_words";
		int trainingSamples=p.getAllFeatures2(path, "training_data");
		//int testingSamples= p.getAllFeatures2(path2, "testing_data");
//		int trainingSamples=2400;
//		int testingSamples=840;
//		String path= "images\\nfeatures\\hear\\hear (50).jpg";
//		String a= p.getFeatures(path, 1);
//		System.out.println(a);
		
	}

}
