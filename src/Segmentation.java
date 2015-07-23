import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.highgui.Highgui;

import java.util.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.image.BufferedImage;
public class Segmentation {
	Mat word;
	Mat colored;
	public Segmentation(String filepath){
		word= Highgui.imread(filepath,Highgui.IMREAD_COLOR);
		colored= word.clone();
		//Imgproc.cvtColor(word, colored, Imgproc.COLOR_GRAY2RGB);
	
	}
	
	public void createMark(int i, int j, String color, Mat image){
		double[] marker={0,0,0};
		switch(color){
			case "blue": marker[0]=255; break; 
			case "green": marker[1]= 255;break;
			case "red": marker[2]=255;break;
		}
		image.put(i, j, marker);
		image.put(i-1, j, marker);
		image.put(i, j-1, marker);
		image.put(i+1, j, marker);
		image.put(i, j+1, marker);
	}
	
	/**
	 * 
	 * @param word  Mat to be converted into binary image (0-255)
	 */
	public void convertToBinary(Mat word){
		int i, j;
		for(i=0;i<word.rows();i++){
			for(j=0; j< word.cols();j++){
				int c  = (int)word.get(i, j)[0];
				double[] white= {255, 255,255};
				double[] black= {0,0,0};
				if(c <20){
					word.put(i, j, black);
					
				}
				else{
					word.put(i, j, white);
				}
			}
		}
	}
	/**
	 * Detects intersection once found three neighboring pixels
	 */
	public void detectIntersection(){
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();  
		int i,j;
		//convert to binary
		this.convertToBinary(word);
		/**
		 * P1 P2 P3
		 * P4 C  P5
		 * P6 P7 P8
		 * 
		 */
		int countNeighbors;
		for( i=1;i<word.rows()-1;i++){
			for( j=1;j<word.cols()-1;j++){
				countNeighbors=0;
				int p1 = (int)word.get(i-1, j-1)[0];
				int p2 = (int)word.get(i-1, j)[0];
				int p3 = (int)word.get(i-1, j+1)[0];
				int p4 = (int)word.get(i, j-1)[0]; 
				int p5 = (int)word.get(i, j+1)[0];
				int c  = (int)word.get(i, j)[0];
				int p6 = (int)word.get(i+1, j-1)[0];
				int p7 = (int)word.get(i+1, j)[0];
				int p8 = (int)word.get(i+1, j+1)[0];
				
				if(p1==0) ++countNeighbors;
				if(p2==0) ++countNeighbors;
				if(p3==0) ++countNeighbors;
				if(p4==0) ++countNeighbors;
				if(p5==0) ++countNeighbors;
				if(p6==0) ++countNeighbors;
				if(p7==0) ++countNeighbors;
				if(p8==0) ++countNeighbors;
				
				//System.out.print(c+",");
				if(c==0 && countNeighbors==3){
					//color
					//System.out.println(c);
					createMark(i, j, "blue", colored);
				}
			}
		}
		//Imgproc.threshold(word, word, 200, 255, Imgproc.THRESH_BINARY);
		
		Highgui.imwrite("images/segmentation/red.jpg",colored);
		Highgui.imwrite("images/segmentation/orig.jpg",word);
	}
	public void detectEndpoints(){
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();  
		int i,j;
		//convert to binary
		this.convertToBinary(word);
		/**
		 * P1 P2 P3
		 * P4 C  P5
		 * P6 P7 P8
		 * 
		 */
		int countNeighbors;
		for( i=1;i<word.rows()-1;i++){
			for( j=1;j<word.cols()-1;j++){
				countNeighbors=0;
				int p1 = (int)word.get(i-1, j-1)[0];
				int p2 = (int)word.get(i-1, j)[0];
				int p3 = (int)word.get(i-1, j+1)[0];
				int p4 = (int)word.get(i, j-1)[0]; 
				int p5 = (int)word.get(i, j+1)[0];
				int c  = (int)word.get(i, j)[0];
				int p6 = (int)word.get(i+1, j-1)[0];
				int p7 = (int)word.get(i+1, j)[0];
				int p8 = (int)word.get(i+1, j+1)[0];
				
				if(p1==0) ++countNeighbors;
				if(p2==0) ++countNeighbors;
				if(p3==0) ++countNeighbors;
				if(p4==0) ++countNeighbors;
				if(p5==0) ++countNeighbors;
				if(p6==0) ++countNeighbors;
				if(p7==0) ++countNeighbors;
				if(p8==0) ++countNeighbors;
				
				//System.out.print(c+",");
				if(c==0 && countNeighbors==1){
					//color
					//System.out.println(c);
					createMark(i, j, "red", colored);
				}
			}
		}
		//Imgproc.threshold(word, word, 200, 255, Imgproc.THRESH_BINARY);
		
		Highgui.imwrite("images/segmentation/red.jpg",colored);
		Highgui.imwrite("images/segmentation/orig.jpg",word);
	}
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Segmentation a = new Segmentation("images/training_words_thinned/acre/word (27).jpg");
		a.detectEndpoints();
		a.detectIntersection();
	}
}

