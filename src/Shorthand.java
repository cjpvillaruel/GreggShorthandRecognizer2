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
	double area, centerOfMass;
	String svmRes, annRes, bnRes;
	File file;
	int id;
	
	public Shorthand(File file, String word, int id){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.file=file;
		this.word= word;
		this.filepath = file.getAbsolutePath();
		image= Highgui.imread(this.filepath,Highgui.IMREAD_GRAYSCALE);
		this.id= id;
	}
	public Shorthand(File file){
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
	public String getPath(){
		return filepath;
	}
	public Mat getImage(){
		return image;
	}
	
}
