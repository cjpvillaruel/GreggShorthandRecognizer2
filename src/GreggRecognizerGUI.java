import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

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
			tabbedPane.add("Word recognition", new Panel1());
			this.tabbedPane.setBounds(100, 150, 600, 400);
			//tabbedPane.add("Test", new TestPanel());
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
		ClassyButton sample;
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
			
			sample= new ClassyButton("hello", "orange");
			this.add(sample);
			this.setBackground(Color.WHITE);
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
	
	class Train implements Runnable{
		String path;
		TrainPanel trainpanel;
		public Train(String filePath, TrainPanel trainpanel){
			this.path= filePath;
			this.trainpanel= trainpanel;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Preprocessing p = new Preprocessing(30);
        	int trainingSamples=p.getAllFeatures(path, "training_data");
        	trainpanel.updateTextArea("Done.\nTraining SVM");
        	ML train= new ML(trainingSamples, 30, trainpanel);
		}
		
	}
	class TrainPanel extends JPanel implements ActionListener{
		JButton trainButton, selectFileButton, loadButton;
		JLabel statusLabel, panelLabel;
		JFileChooser fileChooser;
		JTextField path;
		JTextArea textArea;
		Panel1 panel;
		/**
		 * 
		 * @param panel Panel1
		 */
		public TrainPanel(Panel1 panel){
			this.panel= panel;
			//this.setLayout(null);
			//this.setLayout( new BorderLayout ());
			this.setBorder(BorderFactory.createTitledBorder("Training"));
			this.textArea= this.panel.getTextArea();
			//this.setBackground(Color.white);
			
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
		    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    path= new JTextField(fileChooser.getCurrentDirectory().toString()+"images\\training_words_thinned");
			//path.setBounds(70, 50, 100, 30);
			path.setColumns( 15 );
			this.add(path);
		
			//adding select button
			selectFileButton= new JButton("Browse");
			selectFileButton.addActionListener(this);
			
			this.add(selectFileButton);
			
			trainButton= new JButton("Train");
			this.add(trainButton);
			trainButton.addActionListener(this);
			
			System.out.println("hi");
			
		}
		
		public void updateTextArea (String text) {
			
		    JTextArea area= this.panel.getTextArea();
		    area.append(text);
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
			else if(e.getSource() == trainButton){
				//preprocess images
				
				//thinning
				//get features
				String filepath= path.getText();
				
				
				updateTextArea("Getting features...\n");
				System.out.println("Getting features");
				//panel.textArea.append("Done.\n");
				Train t= new Train(filepath, this);
				SwingUtilities.invokeLater(t);
				
			}
		
		}
	}
	

	class Panel1 extends JPanel implements ActionListener{	
		JButton trainButton, selectFileButton, loadButton;
		JLabel statusLabel;
		JFileChooser fileChooser;
		JTextField path;
		JTextArea textArea;
		PrintStream con;
		TrainPanel trainpanel;
		TestPanel testpanel;
		RecognizerPanel rpanel;
		
		public Panel1(){
			this.setLayout(null);			
			//status textArea
			textArea = new JTextArea();
			textArea.setEditable(false);
			

			JScrollPane scroll = new JScrollPane (textArea, 
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setBounds(50, 270, 500, 90);
			this.add(scroll);
			
			this.trainpanel = new TrainPanel(this);
			trainpanel.setBounds(0, 0, 300, 100);
			this.add(trainpanel);
			
			this.testpanel = new TestPanel(this);
			this.testpanel.setBounds(300, 0, 300, 100);
			this.add(testpanel);
			this.setBackground(Color.white);
			
			statusLabel= new JLabel("Status:");
			statusLabel.setBounds(50, 240, 100, 30);
			this.add(statusLabel);
			
			rpanel= new RecognizerPanel();
			rpanel.setBounds(0, 100, 600, 150);
			this.add(rpanel);
		}
		public PrintStream getConsole(){
			return this.con;
		}
		public JTextArea getTextArea(){
			
			return this.textArea;
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
		
		}
	}
	
	class RecognizerPanel extends JPanel implements ActionListener{
		JFileChooser fileChooser;
		JButton loadButton, rButton;
		JPanel imagePanel;
		Image image;
		JLabel jLabel;
		String path;
		WordDB db;
		JTable table;
		FileDialog fd;
		public RecognizerPanel(){
			db= new WordDB();
			this.setLayout(null);
			loadButton = new JButton("Load Image");
			this.add(loadButton);
			loadButton.addActionListener(this);
			loadButton.setBounds(65, 110, 100, 30);
			path="";
			
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("./images"));
		    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    
		    imagePanel= new JPanel();
		    imagePanel.setBackground(Color.white);
		    jLabel = new JLabel("Select Image");
		    imagePanel.add(jLabel);
		    imagePanel.setBounds(50, 10, 150, 90);
		    
		    this.add(imagePanel);
		    
		   
		    rButton= new JButton("Recognize");
		    rButton.setBounds(220, 60, 120, 30);
		    this.add(rButton);
		    rButton.addActionListener(this);
		    String[] columnNames = {"ANN",
                    "SVM",
                    "BN",
                   };
		    Object[][] data = {{"","",""}};
		    TableModel model = new DefaultTableModel(data, columnNames);
		    table = new JTable(model);
		
		    JScrollPane scroll = new JScrollPane (table);
			scroll.setBounds(350, 30, 230, 39);
			
			this.add(scroll);
			
			fd = new FileDialog(new JFrame(), "Choose a file", FileDialog.LOAD);
			
			fd.setDirectory("C:\\");
			
		}
		private void displayResults(double[] results) throws ClassNotFoundException, SQLException{
			//open database to fetch word
			for(int i=0;i<3;i++){
				table.setValueAt(db.getWord((int)results[i]), 0, i); 
			}
			
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource()== loadButton){
				//int a= fileChooser.showOpenDialog(this);
				fd.setVisible(true);
				
				File file= new File(fd.getDirectory(), fd.getFile());

				  try {                
				    	image = ImageIO.read(file);
				    	ImageIcon imageIcon = new ImageIcon(image);
				        jLabel.setIcon(imageIcon);
				        jLabel.setText("");
				        path= file.getPath();
				    } catch (IOException ex) {
				    }

			}
			
			else if(e.getSource()== rButton){
				//find features
				Preprocessing p= new Preprocessing();
				String[] featureStr= p.getFeatures(path, 1).split("=");
				String[] features= featureStr[0].split(",");
				Mat featuresMat= new Mat(1,features.length-1, CvType.CV_32F);
				
				for(int i=0;i< features.length;i++){
					double a= Double.parseDouble(features[i]);
					featuresMat.put(0,i,a );
				}
				System.out.println(featuresMat.dump());
				double results[]= {0,0,0};
				ML ml= new ML();
				try {
					System.out.println("hehe");
					results=ml.predict(featuresMat,1);
					System.out.println(results[0] +" "+results[1]+" "+ results[2]);
					
					//displayResults
					
					try {
						displayResults(results);
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
				//recognize
			}
			
		}
		
	}
	class TestPanel extends JPanel implements ActionListener{	
		JButton testButton, selectFileButton;
		JTextField numClasses;
		JFileChooser fileChooser;
		ML mLearning;
		Panel1 panel;
		JTextField path;
		
		public TestPanel(Panel1 panel){
			//this.setLayout(null);
			this.setBorder(BorderFactory.createTitledBorder("Test"));
			this.addComponents();
			this.panel= panel;
			
			
			 
	      
			
		}
		
		private void addComponents(){
			
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
		    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    path= new JTextField(fileChooser.getCurrentDirectory().toString()+"images\\training_data");
			//path.setBounds(70, 50, 100, 30);
			path.setColumns( 15 );
			this.add(path);
			
			selectFileButton= new JButton("Browse");
			selectFileButton.addActionListener(this);
			this.add(selectFileButton);
			
			testButton = new JButton("Test");
			//testButton.setBounds(200, 50, 150, 40);
			this.add(testButton);
			testButton.addActionListener(this);
			
			numClasses= new JTextField(10);
			//testButton.setBounds(200, 50, 150, 40);
			//this.add(numClasses);	
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
			else if(e.getSource()== testButton){
				
				Preprocessing p= new Preprocessing(30);
				String filepath= path.getText();
				int testingSamples= p.getAllFeatures(filepath, "testing_data");
				
//				p.thinAllWords("images/training_words");
//				p.thinAllWords("images/testing_words");
				mLearning = new ML( testingSamples, 30);
				
			}
		}
	}