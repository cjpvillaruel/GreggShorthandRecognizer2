import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.sql.*;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;


public class GreggRecognizerGUI extends JFrame {
	MainPanel mainPanel;
	
	public GreggRecognizerGUI(){
		super("Gregg Shorthand Recognizer");
		this.addComponents();
		// get screen dimensions using Toolkit and Dimension classes.
		Toolkit kit = Toolkit.getDefaultToolkit(); 
		Dimension screenSize = kit.getScreenSize(); 
		
		this.setSize( 800, 600 );   
		
	
		// centers the frame in screen
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true); 
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
	}
	private void addComponents(){
		mainPanel = new MainPanel();
		Container c = this.getContentPane();
		c.add(mainPanel);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GreggRecognizerGUI gui= new GreggRecognizerGUI();
		
	}

}

	class MainPanel extends JPanel{
		private Image background;
		JTabbedPane tabbedPane;
		
		public MainPanel(){
			java.net.URL imgURL = getClass().getResource("background/bg.jpg");
			background = Toolkit.getDefaultToolkit().getImage(imgURL  );
			this.setLayout(null);
			this.addComponents();
		}
		public void addComponents(){
			
			tabbedPane = new JTabbedPane();
			tabbedPane.addTab("Preprocess", new PreprocessPanel());
			tabbedPane.add("Train", new TrainPanel());
			tabbedPane.add("Test", new TestPanel());
			
			
			JLabel hi= new JLabel("Hi");
			tabbedPane.setBounds(100, 150, 600, 400);
			this.add(tabbedPane);
		}
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
		}
	}

	class PreprocessPanel extends JPanel implements ActionListener{
		JComboBox setList, isTrainingData;
		JLabel set, status;
		JFileChooser fileChooser;
		JTextField path;
		JButton selectFileButton;
		JButton preprocessButton;
		Preprocessing preprocess;
		JButton preprocessAll;
		public PreprocessPanel(){
			String[] list= {"1","2","3","4"};
			setList= new JComboBox(list);
			set= new JLabel("Select Set");
			preprocessButton= new JButton("crop");
			this.add(set);
			this.add(setList);
			String[] dataType= {"Training", "Testing"};
			isTrainingData= new JComboBox(dataType);
			this.add(isTrainingData);
			//adding select button
			selectFileButton= new JButton("Select Folder");
			//selectFileButton.setBounds(340, 50, 150, 30);
			selectFileButton.addActionListener(this);
			
			this.add(selectFileButton);
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
		    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    
		    path= new JTextField(fileChooser.getCurrentDirectory().toString()+"images\\training_data");
			//path.setBounds(70, 50, 250, 30);
			//this.add(path);
			this.add(preprocessButton);
			preprocessButton.addActionListener(this);
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			preprocess= new Preprocessing();
			
			status= new JLabel("");
			this.add(status);
			
			preprocessAll= new JButton("Crop All DataSet");
			this.add(preprocessAll);
			preprocessAll.addActionListener(this);
			
		}
		@Override
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()== selectFileButton){
				int a= fileChooser.showOpenDialog(this);
				if (a == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					path.setText(file.getPath());
				}
			}
			else if(e.getSource()== preprocessButton){
				String destinationPath="images/", sourcePath="images/";
				if(isTrainingData.getSelectedIndex() == 0){
					destinationPath+="training_words/";
					sourcePath+="training_data/";
				}
				else{
					destinationPath+="testing_words/";
					sourcePath+="testing_data/";
				}
				//System.out.println(setList.getSelectedIndex() +1);
				status.setText("Preprocessing");
				preprocess.cropImages(setList.getSelectedIndex()+1,sourcePath, destinationPath);
				//preprocess.cropImages(1,"images/testing_data/", "images/testing_words/");
				status.setText("Preprocessing done.");

			}
			else if(e.getSource()== preprocessAll){
				String destinationPath="images/", sourcePath="images/";
				if(isTrainingData.getSelectedIndex() == 0){
					destinationPath+="training_words/";
					sourcePath+="training_data/";
				}
				else{
					destinationPath+="testing_words/";
					sourcePath+="testing_data/";
				}
				for(int i=1;i<4;i++){
					status.setText("Preprocessing");
					System.out.println(i);
					preprocess.cropImages(i,sourcePath, destinationPath);
					status.setText("Preprocessing done.");
				}
			}
		}
		
	}
	class TrainPanel extends JPanel implements ActionListener{
		JButton trainButton, selectFileButton, annButton, svmButton, bnButton;
		JLabel statusLabel;
		JFileChooser fileChooser;
		JTextField path;
		public TrainPanel(){
			this.setLayout(null);
			
			trainButton= new JButton("Train");
			trainButton.setBounds(100, 150, 100, 50);
			//this.add(trainButton);
			
			statusLabel= new JLabel("hi");
			statusLabel.setBounds(100, 250, 200, 200);
					
			
			//adding select button
			selectFileButton= new JButton("Select Folder");
			selectFileButton.setBounds(340, 50, 150, 30);
			selectFileButton.addActionListener(this);
			
			this.add(selectFileButton);
			
			
			annButton= new JButton("<html>"+"Train with\n ANN".replace("\n", "<br>")+"</html>");
			annButton.setBounds(100, 150, 100, 100);
			this.add(annButton);
			annButton.addActionListener(this);
			
			
			svmButton= new JButton("<html>"+"Train with\n SVM".replace("\n", "<br>")+"</html>");
			svmButton.setBounds(250, 150, 100, 100);
			this.add(svmButton);
			svmButton.addActionListener(this);
			
			bnButton= new JButton("<html>"+"Train with\n BN".replace("\n", "<br>")+"</html>");
			bnButton.setBounds(400, 150, 100, 100);
			this.add(bnButton);
			bnButton.addActionListener(this);
			
			
			this.setBackground(Color.white);
			
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
		    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    
		    path= new JTextField(fileChooser.getCurrentDirectory().toString()+"images\\training_data");
			path.setBounds(70, 50, 250, 30);
			this.add(path);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()== selectFileButton){
				int a= fileChooser.showOpenDialog(this);
				if (a == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					path.setText(file.getAbsolutePath());
				}
			}
			else if(e.getSource()== annButton){
				//training ANN
				annButton.setEnabled(false);
				
			}
		}
	}
	class TestPanel extends JPanel implements ActionListener{	
		JButton testButton;
		JTextField numClasses;
		ML mLearning;
		public TestPanel(){
			//this.setLayout(null);
			this.setBackground(Color.white);
			this.addComponents();
			
			
			
		}
		
		private void addComponents(){
			testButton = new JButton("Test");
			//testButton.setBounds(200, 50, 150, 40);
			this.add(testButton);
			testButton.addActionListener(this);
			
			numClasses= new JTextField(10);
			//testButton.setBounds(200, 50, 150, 40);
			this.add(numClasses);
			
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()== testButton){
				//fill table
				int classes=Integer.parseInt(numClasses.getText());
				Preprocessing p= new Preprocessing(classes);
				int trainingSamples=p.getAllFeatures("images/training_words/", "training_data");
				int testingSamples= p.getAllFeatures("images/testing_words/", "testing_data");
				
				mLearning = new ML(trainingSamples, classes, testingSamples);
				
				mLearning.ann();
				mLearning.predictAnn();
//				mLearning.svm();
				//mLearning.bayes();
				
//				for(int i=0;i<mLearning.svmPredicted.length;i++){
//					System.out.println(mLearning.svmPredicted[i]+" "+mLearning.bayesPredicted[i]);
//				}
				
			}
		}
	}