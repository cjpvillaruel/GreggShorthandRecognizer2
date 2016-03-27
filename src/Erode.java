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
public class Erode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String path= "images/dataset/p1/dataset1 (10).jpg";
		//load image
		Mat word = Highgui.imread(path,Highgui.IMREAD_GRAYSCALE);
		Mat bw= new Mat();
		Imgproc.threshold(word, word, 220, 255, Imgproc.THRESH_BINARY);
		//dilate
		Mat structElement = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3,3));
		Imgproc.erode(word, word, structElement);
		Highgui.imwrite("images/dataset/p1result/result.jpg",word);
	}

}
