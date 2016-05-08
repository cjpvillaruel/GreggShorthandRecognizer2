import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import java.awt.Desktop;
class DocumentReaderPanel extends JPanel implements ActionListener{
	private MainPanel1 card;
	private JLabel title, heading, rLabel;
	private JPanel imagePanel;
	private ClassyButton rButton, selectImage;
	private JTextArea textArea;
	private JPanel navBar;
	private JButton backButton;
	private JComboBox mlSelect;
	private FileDialog fd;
	private File[] files;
	private JLabel imageLabel;
	private WordRecognizer recognizer;
	private static final int MARGIN= 5;
	private String transcribed="";
	
	public DocumentReaderPanel(MainPanel1 card){
		this.setBackground(Color.WHITE);
		this.card= card;
		this.setLayout(null);
		this.recognizer = new WordRecognizer();
		
		this.addHeaderComponents();
		this.addComponents();
			 
	}
	private void addComponents(){
		imagePanel= new JPanel();
		imagePanel.setBounds(50, 180, 330, 380);
		imagePanel.setBackground(Color.orange);
		this.add(imagePanel);
		
		selectImage= new ClassyButton("Select image", "white");
		imagePanel.add(selectImage);
		selectImage.addActionListener(this);
		
		imageLabel= new JLabel();
		imagePanel.add(imageLabel);

		String[] list= {"ANN","Naive Bayes","SVM"};
		mlSelect= new JComboBox(list);
		mlSelect.setBounds(400, 250,100,30);
		this.add(mlSelect);
		
		rButton= new ClassyButton("Translate","blue");
		rButton.setBounds(400, 300, 100, 30);
		this.add(rButton);
		rButton.addActionListener(this);
		
		textArea= new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane (textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBounds(530, 250, 230, 130);
		this.add(scroll);
		
		rLabel= new JLabel("Result:");
		rLabel.setBounds(530, 200, 200, 30);
		this.add(rLabel);
		
		
	
		
		fd = new FileDialog(new JFrame(), "Choose a file", FileDialog.LOAD);
		fd.setDirectory(".");
	}
	private void addHeaderComponents(){
		this.title= new JLabel("Gregg Shorthand Recognizer");
		this.title.setBounds(250, 50, 300, 30);
		this.title.setFont(new Font("Sans Serif", Font.BOLD, 20));
		this.add(title);		
		
		
		this.heading= new JLabel("Document Reader");
		this.heading.setBounds(120, 125, 200, 30);
		this.heading.setFont(new Font("Sans Serif", Font.BOLD, 16));
		this.heading.setForeground(Color.WHITE);
		this.add(heading);
		
		backButton= new JButton();
		backButton.setBounds(50, 117, 45, 45);
		this.add(backButton);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false); 
		backButton.setOpaque(false);
		backButton.addActionListener(this);
		Image img;
		//java.net.URL imgURL = getClass().getResource("../images/ui/word.png");
		try {
			img= ImageIO.read(new File("images/ui/button.png"));
			backButton.setIcon(new ImageIcon(img));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		navBar = new JPanel();
		navBar.setBounds(0,120,800, 40);
		navBar.setBackground(new Color(0, 174, 239));
		this.add(navBar);
		
		
	}
	/**
	* This method resizes the given image using Image.SCALE_SMOOTH.
	*
	* @param image the image to be resized
	* @param width the desired width of the new image. Negative values force the only constraint to be height.
	* @param height the desired height of the new image. Negative values force the only constraint to be width.
	* @param max if true, sets the width and height as maximum heights and widths, if false, they are minimums.
	* @return the resized image.
	*/
	public static Image resizeImage(Image image, int width, int height, boolean max) {
	  if (width < 0 && height > 0) {
	    return resizeImageBy(image, height, false);
	  } else if (width > 0 && height < 0) {
	    return resizeImageBy(image, width, true);
	  } else if (width < 0 && height < 0) {
	    System.out.println("Setting the image size to (width, height) of: ("
	            + width + ", " + height + ") effectively means \"do nothing\"... Returning original image");
	    return image;
	    //alternatively you can use System.err.println("");
	    //or you could just ignore this case
	  }
	  int currentHeight = image.getHeight(null);
	  int currentWidth = image.getWidth(null);
	  int expectedWidth = (height * currentWidth) / currentHeight;
	  //Size will be set to the height
	  //unless the expectedWidth is greater than the width and the constraint is maximum
	  //or the expectedWidth is less than the width and the constraint is minimum
	  int size = height;
	  if (max && expectedWidth > width) {
	    size = width;
	  } else if (!max && expectedWidth < width) {
	    size = width;
	  }
	  return resizeImageBy(image, size, (size == width));
	}

	/**
	* Resizes the given image using Image.SCALE_SMOOTH.
	*
	* @param image the image to be resized
	* @param size the size to resize the width/height by (see setWidth)
	* @param setWidth whether the size applies to the height or to the width
	* @return the resized image
	*/
	public static Image resizeImageBy(Image image, int size, boolean setWidth) {
	  if (setWidth) {
	    return image.getScaledInstance(size, -1, Image.SCALE_SMOOTH);
	  } else {
	    return image.getScaledInstance(-1, size, Image.SCALE_SMOOTH);
	  }
	}
	private Rect intersect (Rect A, Rect B){
		int left = Integer.max(A.x, B.x);
		int top  = Integer.max(A.y, B.y);
		int right = Integer.min(A.x + A.width, B.x + B.width);
		int bottom = Integer.min(A.y + A.height, B.y + B.height);
		if(left <= right && top <= bottom) return new Rect(left, top, right - left, bottom - top);
		else return new Rect();
	}
	
	private Rect merge (Rect A, Rect B){
		int left = Integer.min(A.x, B.x);
		int top  = Integer.min(A.y, B.y);
		int right = Integer.max(A.x + A.width, B.x + B.width);
		int bottom = Integer.max(A.y + A.height, B.y + B.height);
		return new Rect(left, top, right - left, bottom - top);
	}
	private boolean isOverlapping(Rect a, Rect b,Mat image){
		//if one box is inside the other
		Rect rect1= a.clone();
		Rect rect2= b.clone();
		rect1.x-=3;
		rect1.y-=3;
		
		rect2.x-=3;
		rect2.y-=3;
		
		rect1.height+=3;
		rect1.width+=3;
		
		rect2.height+=3;
		rect2.width+=3;
		
		Rectangle a1 = new Rectangle(rect1.x, rect1.y, rect1.width, rect2.height);
		Rectangle a2 = new Rectangle(rect1.x, rect1.y, rect1.width, rect2.height);
		
		
		if(intersect(rect1 , rect2) == rect1){
			
		}
		if(intersect(rect1,rect2).area() > 0){
			Rect newrect = merge(rect1 , rect2);
			Core.rectangle(image,new Point(newrect.x,newrect.y),new Point(newrect.x+newrect.width,newrect.y+newrect.height), new Scalar(0, 244, 255),1);
	        return true;
		}
		return false;
	}
	private void sortContours(ArrayList<Rect> boxes){
		Collections.sort( boxes, new Comparator<Rect>() {
		       public int compare(Rect x1, Rect x2) {
		         int result = Double.compare(x1.x, x2.x);
		         if ( result == 0 ) {
		           // both X are equal -> compare Y too
		           result = Double.compare(x1.y, x2.y);
		         } 
		         return result;
		      }
		    });
	}
	
	private void findOverlap(ArrayList<Rect> boxes,Mat image){
		ArrayList<Rect> boxesCopy= (ArrayList<Rect>) boxes.clone();
		for(int i=0; i<boxes.size();i++){
			Rect rect1= boxes.get(i);
			Rectangle a= new Rectangle(rect1.x-MARGIN,rect1.y-MARGIN,rect1.width+MARGIN*2,rect1.height+MARGIN*2);
			//System.out.println(a.height+"=="+ (rect1.height+rect1.y));
			for(int j=0;j<boxes.size();j++){
				Rect rect2= boxes.get(j);	
				Rectangle b= new Rectangle(rect2.x-MARGIN,rect2.y-MARGIN,rect2.width+MARGIN*2,rect2.height+MARGIN*2);
				
				if(i!=j && !(a.equals(b))){
					if(a.contains(b)){
						boxes.remove(j);
						boxesCopy.remove(j);
						j--;
						System.out.println(boxes.size());
						Core.rectangle(image,new Point(rect1.x-3,rect1.y-3),new Point(rect1.x+rect1.width+3,rect1.y+rect1.height+3),new Scalar(0, 0, 255),1);
//						
					}
					
					else if(a.intersects(b)){
						System.out.println("intersects");
						Rectangle union= a.union(b);
						boxes.remove(j);
						boxesCopy.remove(j);
						boxes.remove(i);
						boxesCopy.remove(i);
						
						Rect newRect= new Rect(union.x, union.y, union.width, union.height);
//						Core.rectangle(image,new Point(newRect.x,newRect.y),new Point(newRect.x+newRect.width,newRect.y+newRect.height),new Scalar(255, 0, 0),1);		
//						Core.rectangle(image,new Point(rect1.x-3,rect1.y-3),new Point(rect1.x+rect1.width+3,rect1.y+rect1.height+3),new Scalar(0, 0, 255),1);
//						Core.rectangle(image,new Point(rect2.x-3,rect2.y-3),new Point(rect2.x+rect2.width+3,rect2.y+rect2.height+3),new Scalar(255, 0, 0),1);
//						
						boxesCopy.add(newRect);
						i--;
						//break;
//						j--;
					}
				}
	        }
		}
		
		for(int i=0; i<boxesCopy.size();i++){
			Rect rect1= boxesCopy.get(i);
			Rectangle a= new Rectangle(rect1.x-3,rect1.y-3,rect1.width+6,rect1.height+6);
			//System.out.println(a.height+"=="+ (rect1.height+rect1.y));
			for(int j=boxesCopy.size()-1;j>=0;j--){
				Rect rect2= boxesCopy.get(j);	
				Rectangle b= new Rectangle(rect2.x-3,rect2.y-3,rect2.width+6,rect2.height+6);
				
				if(i!=j && !(a.equals(b))){
					if(a.contains(b)){
						boxesCopy.remove(j);
						j++;
						
					}
				}
	        }
		}		
		//sort boxes
		//sortContours(boxesCopy);
		for(int i=0; i<boxesCopy.size();i++){
			Rect rect1= boxesCopy.get(i);
			//Core.rectangle(image,new Point(rect1.x-3,rect1.y-3),new Point(rect1.x+rect1.width+3,rect1.y+rect1.height+3),new Scalar(0, 0, 255),1);
			//recognize the area in the rectangle
			Rect letter= new Rect(rect1.x-3, rect1.y-3, rect1.width+3, rect1.height+3);
			System.out.println("rows"+image.rows()+" and "+ (letter.y+letter.width));
    		System.out.println("cols"+image.cols()+" and "+ (letter.x+letter.height));
			if(image.cols()< letter.x+letter.height)
				continue;
    		Mat result = image.submat(letter);
    		
    		Imgproc.cvtColor(result, result, Imgproc.COLOR_RGB2GRAY);
			
			//print image
			Highgui.imwrite("images/wordseg/img"+i+".jpg", result);
			result= Highgui.imread("images/wordseg/img"+i+".jpg", Highgui.IMREAD_GRAYSCALE);
			
			Shorthand word = new Shorthand(result);
			System.out.println(i+" of "+boxesCopy.size());
			
			try {
				recognizer.recognize(word);
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Core.rectangle(image,new Point(rect1.x-3,rect1.y-3),new Point(rect1.x+rect1.width+3,rect1.y+rect1.height+3),new Scalar(0, 0, 255),1);
			
			//put the label into the image
			// Core.putText(image, word.bnRes, new Point(rect1.x,rect1.y), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0,0,0),1); 
			String selectedML= (String) mlSelect.getItemAt(mlSelect.getSelectedIndex());
			//System.out.println(selectedML);
			if(selectedML == "ANN"){
				Core.putText(image, word.annRes, new Point(rect1.x,rect1.y), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0,0,0),1); 
				this.transcribed+= word.annRes+" ";
			}
			if(selectedML == "SVM"){
				Core.putText(image,word.svmRes, new Point(rect1.x,rect1.y), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0,0,0),1); 
				this.transcribed+= word.svmRes+" ";
			}
			if(selectedML == "Naive Bayes"){
				Core.putText(image, word.bnRes, new Point(rect1.x,rect1.y), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0,0,0),1); 
				this.transcribed+= word.bnRes+" ";
			}
		}
	}
	
	private void boundDocument(String path){
		Mat image= Highgui.imread(path,Highgui.IMREAD_GRAYSCALE);
		Mat data1= Highgui.imread(path);
		Mat image2= Highgui.imread(path,Highgui.IMREAD_COLOR);
        Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_OTSU);
        Mat horizontal = Mat.zeros(image.rows(), 1, CvType.CV_16S);
        Mat vertical = Mat.zeros(1, image.cols(), CvType.CV_16S);
        
        //get horizontal histogram
        for(int i=0;i< image.rows();i++){
        	for(int j=0; j< image.cols();j++){
        		double val = image.get(i, j)[0];
        		if(val == 0){
        			double h= horizontal.get(i, 0)[0]+1;
        			horizontal.put(i, 0, h);
        			double v= vertical.get(0, j)[0]+1;
        			vertical.put(0, j, v);
        		}
        	}
        }
        Mat hori = new Mat(image.rows(),image.cols(), image.type());
        Mat ver = new Mat(image.rows(),image.cols(), image.type());
        hori.setTo(new Scalar(255));
        ver.setTo(new Scalar(255));
        for(int i=0;i< image.rows();i++){
        	for(int j=0; j< image.cols();j++){
        		double h = horizontal.get(i, 0)[0];
        		if(h > 4){
        			double[] color={0,0,255};
        			hori.put(i,j , color);
        		}
        		double v = vertical.get(0, j)[0];
        		if(v > 4){
        			double[] color={0,255,0};
        			ver.put(i,j , color);
        		}
        	}
        }
        
        Highgui.imwrite("images/bound2.png", hori);
        Highgui.imwrite("images/bound.png", ver);
        Mat intersect = new Mat(image.rows(),image.cols(), image.type());
        intersect.setTo(new Scalar(255));
        
        for(int i=0;i< image.rows();i++){
        	for(int j=0; j< image.cols();j++){
        		double h = hori.get(i, j)[0];
        		double v = ver.get(i, j)[0];
        		double[] color={0,255,0};
        		if(v == 0 && h ==0){
        			intersect.put(i,j , color);
        		}		
        	}
        }
       
        Highgui.imwrite("images/bound2.png", intersect);
        System.out.println(horizontal.dump());
	}
	
	private void readDocument(String path){
		transcribed="";
		//read file
		Mat image= Highgui.imread(path,Highgui.IMREAD_GRAYSCALE);
		Mat data1= Highgui.imread(path);
		Mat image2= Highgui.imread(path,Highgui.IMREAD_COLOR);
		//dilation
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_OTSU);
        Mat hierarchy = new Mat();
        double d_min = Double.MAX_VALUE;
        Rect rect_min = new Rect();
      
        MatOfPoint2f  approxCurve = new MatOfPoint2f();
        
		//word segmentation
        Imgproc.threshold(image, image, 220, 128, Imgproc.THRESH_BINARY_INV);
		Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		ArrayList<Rect> boxes=new ArrayList<Rect>();
		
		for (MatOfPoint contour : contours) { 		
            MatOfPoint2f contour2f = new MatOfPoint2f( contour.toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            //Core.rectangle(image2,new Point(rect.x-3,rect.y-3),new Point(rect.x+rect.width+3,rect.y+rect.height+3),new Scalar(0, 255, 0),1);
            boxes.add(rect);
    	}
		
		//find overlapping rectangles
		findOverlap(boxes, image2);
		

		Highgui.imwrite("images/segmentedimage.png", image2);
		
		Image displayImage;
		try {
			displayImage = ImageIO.read(new File("images/segmentedimage.png"));
			
	        Desktop dt = Desktop.getDesktop();
	        dt.open(new File("images/segmentedimage.png"));
	        displayImage=resizeImage(displayImage, 300, 420, true);
	    	ImageIcon imageIcon = new ImageIcon(displayImage);
	        imageLabel.setIcon(imageIcon);
	        imageLabel.setText("");
	      
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
		//recognize each word segmented 
		this.textArea.setText(this.transcribed);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()== rButton){
			//
			String path = files[0].getAbsolutePath();
			readDocument(path);
		}
		else if(e.getSource()== selectImage){
			fd.setVisible(true);
			files = fd.getFiles();
			System.out.println(files[0].getAbsolutePath());
			try {                
		    	Image image = ImageIO.read(files[0]);
		    	
		    	image=resizeImage(image, 300, 420, true);
		    	ImageIcon imageIcon = new ImageIcon(image);
		        imageLabel.setIcon(imageIcon);
		        imageLabel.setText("");
		        //path= file.getPath();
		    } catch (IOException ex) {
		    }
		}
		else if(e.getSource()== backButton){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Menu");
		}
	}
	
	public static void main(String[] args){
		DocumentReaderPanel p= new DocumentReaderPanel(null);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String path= "images/reader/data (2).jpg";
		p.boundDocument(path);
		//p.readDocument("images/Picture1.png");
		Rectangle a = new Rectangle(1, 2, 4, 2);
		Rectangle b = new Rectangle(2,1,1,2);
		Rectangle c = new Rectangle(4,0,1,1);
		Rectangle d = new Rectangle(3,3,1,1);
		Rectangle merge=a.union(b);
		System.out.println(merge.x+" "+merge.y+" "+merge.width+" "+merge.height);
		System.out.println(c.intersects(a) );
		System.out.println(a.contains(d));
	}
}