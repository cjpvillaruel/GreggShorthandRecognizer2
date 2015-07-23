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

public class Word {
	String filename, filepath, word;
	String type; //testing or training
	Mat image;
	double area, centerOfMass;
	
}
