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

public class Shorthand{
	String filename, filepath, word;
	String type; //testing or training
	Mat image;
	//double area, centerOfMass;
	String svmRes, annRes, bnRes;
	File file;
	int id;
	double width,height,x,y, area;
	Mat features;
	public Shorthand(File file, String word, int id){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.file=file;
		this.word= word;
		this.filepath = file.getAbsolutePath();
		image= Highgui.imread(this.filepath,Highgui.IMREAD_GRAYSCALE);
		this.id= id;
	}
	//attributes
	public Shorthand(double width, double height,double x, double y, double area, int id){
		this.width= width;
		this.height= height;
		this.x= x;
		this.y=y;
		this.area= area;
		this.id= id;
	}
	public Shorthand(Mat image){
		this.image = image;
	}
	public Shorthand(Mat features, int index){
		this.features = features;
		this.id = index;
	}
	public Shorthand(File file){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.file = file;
		this.filepath = file.getAbsolutePath();
		image= Highgui.imread(this.filepath,Highgui.IMREAD_GRAYSCALE);
		this.id=0;
	}
	public void setResults(String annRes, String svmRes, String bnRes){
		this.svmRes= svmRes;
		this.annRes= annRes;
		this.bnRes= bnRes;
	}
	public void setId(int id){
		this.id= id;
	}
	public void setFeatures(Mat features){
		this.features = features;
	}
	public String getPath(){
		return filepath;
	}
	public Mat getImage(){
		return image;
	}
	public int getId(){
		return this.id;
	}
	public Mat getFeatures(){
		return this.features;
	}
	
}
