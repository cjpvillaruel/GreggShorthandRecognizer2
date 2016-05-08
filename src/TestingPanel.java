import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Side;

import org.opencv.core.Mat;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.*;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.Utils;

class TestingPanel extends JPanel implements ActionListener, Constants{
	
	final String ann="ANN";
	final String svm="SVM";
	final String nb="NB";
	private ArrayList<Shorthand> testingSamples;
	private ClassyButton  selectFolder1,selectFolder2,selectDest, run,crop;
	private ClassyButton viewConfButton, viewANN, viewSVM, viewBN, viewResults;
	private MainPanel1 card;
	private JButton backButton;
	private JComboBox setList;
	private JDialog d, resultsDialog;
	private JFileChooser srcFolder, destFolder,tFolder;
	private JLabel title, heading, rLabel;
	private JLabel selectLabel1,selectLabel2, selectData;
	private JLabel heading2, heading3;
	private JLabel sLabel, dLabel;    //source and destination label
	private JPanel pPanel,tPanel;
	private JPanel navBar;
	private JPanel resultsPanel, predictedPanel, cPanel, aPanel;
	private JLabel status;
	private JRadioButton train,test,eval;
	private JTable resultsTable, cTable, accuracyTable, summaryTable;
	private JTextArea textArea;
	private JTextField folder1,folder2, destination;
	private JTextArea console;
	private Preprocessing p;
	private String[] wordClasses;
	private TestingResult res;
	private WordDB db;
	private WordRecognizer r;
	private Task task;
	private JFXPanel aGraphPanel,pGraphPanel;
	private LineChart<String,Number> lineChart;
	private Evaluation evalANN, evalNB, evalSVM;
	private double[] trainingTime;
    Scene scene=null;
     CategoryAxis xAxis = null;
	 NumberAxis yAxis = null;
	 BarChart<String,Number> bc = null;
	 	
	public TestingPanel(MainPanel1 card){
		this.setBackground(Color.white);
		this.setLayout(null);
		this.card= card;
		db= new WordDB();
		wordClasses= new String[NUM_CLASSES];
		for(int i=0;i<NUM_CLASSES;i++){
			try {
				wordClasses[i] = db.getWord(i+1);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		addHeaderComponents();
		addComponents();
		p= new Preprocessing(NUM_CLASSES);
		
		testingSamples= new ArrayList<Shorthand>();
		r= new WordRecognizer();
		
		
	}
	private void addComponents(){
		//preprocess panel components
		pPanel = new JPanel();
		pPanel.setBounds(20, 150, 310, 180);
		pPanel.setLayout(null);
		this.add(pPanel);
		
		heading2 = new JLabel("Preprocessing");
		heading2.setBounds(100,5,100,30);
		pPanel.add(heading2);
		
		selectData= new JLabel("Select Dataset:");
		selectData.setBounds(50,30,100,30);
		pPanel.add(selectData);
		String[] list= {"1","2","3","4","5","6"};
		setList= new JComboBox(list);
		setList.setBounds(150, 30, 80, 25);
		pPanel.add(setList);
		
		
		sLabel = new JLabel("Select source folder:");
		sLabel.setBounds(40, 55, 150, 25);
		pPanel.add(sLabel);
		
		folder1= new JTextField(10);
		folder1.setBounds(40, 75, 120, 25);
		pPanel.add(folder1);
		
		selectFolder1 = new ClassyButton("Browse", "white");
		selectFolder1.setBounds(170, 75, 100, 25);
		pPanel.add(selectFolder1);
		
		dLabel = new JLabel("Select destination folder:");
		dLabel.setBounds(40, 95, 200, 25);
		pPanel.add(dLabel);
		
		destination = new JTextField(10);
		destination.setBounds(40, 115, 120, 25);
		pPanel.add(destination);
		
		selectDest = new ClassyButton("Browse", "white");
		selectDest.setBounds(170, 115, 100, 25);
		pPanel.add(selectDest);
		
		crop= new ClassyButton("Crop", "blue");
		crop.setBounds(100, 145, 95, 30);
		pPanel.add(crop);
		
		srcFolder = new JFileChooser();
		srcFolder.setCurrentDirectory(new java.io.File("."));
	    srcFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    
	    destFolder = new JFileChooser();
		destFolder.setCurrentDirectory(new java.io.File("."));
	    destFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		//train and test panel components
		tPanel = new JPanel();
		tPanel.setBounds(20, 340, 310, 210);
//		tPanel.setBackground(Color.red);
		tPanel.setLayout(null);
		this.add(tPanel);
		
		status= new JLabel("");
		//status.setBackground(Color.red);
		status.setBounds(30, 10, 250, 30);
        tPanel.add(status);
        
		train= new JRadioButton("Train");
		test= new JRadioButton("Test");
		eval = new JRadioButton("Evaluate Models");
		ButtonGroup group = new ButtonGroup();
        group.add(train);
        group.add(test);
        group.add(eval);
        GridLayout layout = new GridLayout(1, 0);
        JPanel radioPanel = new JPanel();
      //  train.setBounds(0,0 , 10, 30);
        radioPanel.add(train);
        radioPanel.add(test);
        radioPanel.add(eval);
        
        radioPanel.setBounds(35, 15, 250, 30);
        tPanel.add(radioPanel);
        
        tFolder = new JFileChooser();
		tFolder.setCurrentDirectory(new java.io.File("."));
	    tFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		folder2= new JTextField(10);
		folder2.setBounds(15, 55, 120, 25);
		tPanel.add(folder2);
		
		selectFolder2 = new ClassyButton("Browse", "white");
		selectFolder2.setBounds(140, 55, 60, 25);
		selectFolder2.setMargin(new java.awt.Insets(0, 2,0, 2));
		tPanel.add(selectFolder2);
		
		run= new ClassyButton("Run", "blue");
		run.setBounds(210, 55, 90, 25);
		tPanel.add(run);
		
		console= new JTextArea();		
		console.setEditable(false);
		console.setMargin(new java.awt.Insets(5, 5, 5, 5));
		JScrollPane scrolltextArea = new JScrollPane(console);
		scrolltextArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		scrolltextArea.setBounds(10, 90, 290, 100);
		tPanel.add(scrolltextArea);
		
		selectFolder1.addActionListener(this);
		selectFolder2.addActionListener(this);
		selectDest.addActionListener(this);
		run.addActionListener(this);
		crop.addActionListener(this);
		
		resultsPanel= new JPanel();
		resultsPanel.setLayout(new CardLayout());
		
		predictedPanel= new JPanel();
		predictedPanel.setLayout(null);
		//initialize table
		Image image;
		try {
			image = ImageIO.read(new File("images/data2/result_0.jpg"));
			ImageIcon imageIcon = new ImageIcon(image);
			String[] columnNames = {"Image","Acutal","ANN","SVM","NB"};
			Object[] row= {imageIcon,"","" ,"", ""};
		    Object[][] data = {row};
		    TableModel model = new DefaultTableModel(data, columnNames);
		    resultsTable = new JTable(model){
	            //  Returning the Class of each column will allow different
	            //  renderers to be used based on Class
	            public Class getColumnClass(int column)
	            {
	                return getValueAt(0, column).getClass();
	            }
	        };
	        resultsTable.setRowHeight(60);
	        TableColumnModel columnModel = resultsTable.getColumnModel();
	        columnModel.getColumn(0).setPreferredWidth(80);
	        columnModel.getColumn(1).setPreferredWidth(25);
	        columnModel.getColumn(2).setPreferredWidth(25);
	        columnModel.getColumn(3).setPreferredWidth(25);
	        columnModel.getColumn(4).setPreferredWidth(25);
	        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	        JScrollPane scroll = new JScrollPane(resultsTable);
	        scroll.setBounds(5, 5, 385, 380);
	        predictedPanel.add(scroll);
	        
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//create panel for confusion matrix
		cPanel= new JPanel();
		cPanel.setLayout(null);
		
		cTable= new JTable(NUM_CLASSES+1,NUM_CLASSES+1){
		    @Override
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
		        Component comp = super.prepareRenderer(renderer, row, col);
		        Object value = getModel().getValueAt(row, col);
		       // if (getSelectedRow() == row) {
		        	if(col==0){
		        		comp.setBackground(Color.LIGHT_GRAY);
		        	}
		        	else if( row+1== col){
		        		comp.setBackground(Color.GREEN);
		        	}
		        	else {
		            comp.setBackground(Color.white);
		        	}
		        	
		        	if (getSelectedRow() == row) {
		        		if( row+1== col){
			        		comp.setBackground(Color.GREEN);
			        	}
		        		else{
		        			comp.setBackground(Color.lightGray);  
		        		}       	 
	                }
		        return comp;
		    }
		};
		TableColumn column = null;
	    for (int i = 0; i <= NUM_CLASSES; i++) {
	        column = cTable.getColumnModel().getColumn(i);
	        if (i == 0) {
	            column.setPreferredWidth(50); //sport column is bigger
	        } else {
	            column.setPreferredWidth(30);
	        }
	    }  
	   
		JScrollPane scroll2= new JScrollPane(cTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		cTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//scroll2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		scroll2.setBounds(50, 70, 1100, 550);
		cPanel.add(scroll2);
		
		viewANN= new ClassyButton("ANN", "blue");
		viewSVM= new ClassyButton("SVM", "orange");
		viewBN= new ClassyButton("NB", "blue");
		
		viewANN.setBounds(400, 20, 100, 30);
		viewSVM.setBounds(550, 20, 100, 30);
		viewBN.setBounds(700, 20, 100, 30);
		
		viewANN.addActionListener(this);
		viewSVM.addActionListener(this);
		viewBN.addActionListener(this);
		
		d= new JDialog();
		d.setLayout(null);
		d.setTitle("Confusion Matrices");
		d.setSize(new Dimension(1200, 700));
		Container c= d.getContentPane();
		
		c.setBackground(Color.WHITE);
		d.add(scroll2);
		
		d.add(viewANN);
		d.add(viewSVM);
		d.add(viewBN);
		
		resultsDialog = new JDialog();
		//resultsDialog.setLayout(null);
		resultsDialog.setTitle("Confusion Matrices");
		resultsDialog.setSize(new Dimension(800, 700));
		c= resultsDialog.getContentPane();
		
		
		pGraphPanel = new JFXPanel();
		Platform.runLater(new Runnable(){
            @Override
            public void run() { 
				final CategoryAxis xAxis = new CategoryAxis();
				final NumberAxis yAxis = new NumberAxis(0,100, 10);
				xAxis.setLabel("Word");       
				yAxis.setLabel("Recognition rate (%)");     
				lineChart = new LineChart<String,Number>(xAxis,yAxis);

			        for(int i=0;i<3 ;i++){
				       XYChart.Series series = new XYChart.Series();
				       switch(i){
				       case 0: series.setName("ANN");break;
				       case 1: series.setName("SVM");break;
				       case 2: series.setName("NB");break;
				       }
				       for(int j=0;j<NUM_CLASSES;j++){
				    	   series.getData().add(new XYChart.Data(wordClasses[j], 0));
						}
				       lineChart.getData().add(series);
			        }
					
			        lineChart.getStylesheets().add("chart.css");
			        Scene scene  = new Scene(lineChart,800,600);
		        pGraphPanel.setScene(scene);
            }
        });
		//pGraphPanel.setBounds(20,20,700,400);
		c.add(pGraphPanel);
		
		//create panel and table for accuracy results
		aPanel = new JPanel();
		aPanel.setLayout(null);
		String[] columnNames = {"Word","ANN Precision","ANN Recall","SVM Precision","SVM Recall","BN Precision","BN Recall"};
		Object[] row= {"","","" ,"", "","",""};
	    Object[][] data = {row};
	    TableModel model = new DefaultTableModel(data, columnNames);
	    accuracyTable = new JTable(model);
	    JScrollPane scroll3= new JScrollPane(accuracyTable);
	    scroll3.setBounds(5, 240, 380, 200);
	    //aPanel.add(scroll3);
	    
	    String[] columnNames2 = {" ","ANN","SVM","NB"};
	    Object[][] data2 = {{"ACCURACY","","" ,""},{"BUILD TIME","","" ,""},{"RECALL","","" ,""} };
	    model = new DefaultTableModel(data2, columnNames2);
	    TableModel model2 = new DefaultTableModel(data2, columnNames2);
	    summaryTable = new JTable(model2);
	    JScrollPane scroll4= new JScrollPane(summaryTable);
	    scroll4.setBounds(10, 250, 380, 70);
	    aPanel.add(scroll4);
	    
	    
	    viewConfButton= new ClassyButton("View Confusion Matrix", "blue");
	    viewConfButton.setBounds(200, 350, 180, 30);
	    viewConfButton.addActionListener(this);
	    aPanel.add(viewConfButton);

	    viewResults= new ClassyButton("View all results", "blue");
	    viewResults.setBounds(20, 350, 160, 30);
	    viewResults.addActionListener(this);
	    aPanel.add(viewResults);
	   
		aGraphPanel = new JFXPanel();
		Platform.runLater(new Runnable(){
            @Override
            public void run() { 
        	   xAxis = new CategoryAxis();
        		yAxis = new NumberAxis(0,100, 10);
        		bc = new BarChart<String,Number>(xAxis,yAxis);
				bc.setTitle("Recognition Rate Comparison");
		        xAxis.setLabel("ML");       
		        yAxis.setLabel("Recognition rate");
		        scene = new Scene(bc,320,200);    
                XYChart.Series series1 = new XYChart.Series();
                series1.getData().add(new XYChart.Data(ann, 2));
		        series1.getData().add(new XYChart.Data(svm, 3));
		        series1.getData().add(new XYChart.Data(nb, 6));
		        bc.getData().add(series1);
		      //  bc.setStyle("CHART_COLOR_1: #e9967a;");
		        
		       bc.getStylesheets().add("chart.css");
		        aGraphPanel.setScene(scene);
            }
        });

		
		aGraphPanel.setBackground(Color.green);
		aGraphPanel.setBounds(20,20,360,200);
		aPanel.add(aGraphPanel);
	    
		//System.out.println(aGraphPanel.getScene().rootProperty());
	    resultsPanel.add(aPanel, "Accuracy");
		//resultsPanel.add(cPanel, "Confusion");
		resultsPanel.add(predictedPanel, "Predicted");
		resultsPanel.setBounds(350, 150, 400, 400);
		this.add(resultsPanel);
		
		
		
//		CardLayout cl = (CardLayout)(this.card.getLayout());
//        cl.show(this.card, "Document");
	
	}
	private void addHeaderComponents(){
		this.title= new JLabel("Gregg Shorthand Recognizer");
		this.title.setBounds(250, 50, 300, 30);
		this.title.setFont(new Font("Sans Serif", Font.BOLD, 20));
		this.add(title);
		
		this.heading= new JLabel("Train and Test");
		this.heading.setBounds(120, 105, 200, 30);
		this.heading.setFont(new Font("Sans Serif", Font.BOLD, 16));
		this.heading.setForeground(Color.WHITE);
		this.add(heading);
		
		backButton= new JButton();
		backButton.setBounds(50, 97, 45, 45);
		this.add(backButton);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false); 
		backButton.setOpaque(false);
		backButton.addActionListener(this);
		Image img;
		
		try {
			img= ImageIO.read(new File("images/ui/button.png"));
			backButton.setIcon(new ImageIcon(img));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		navBar = new JPanel();
		navBar.setBounds(0,100,800, 40);
		navBar.setBackground(new Color(0, 174, 239));
		this.add(navBar);
	}
	
	private void readImages2(String folderpath) throws ClassNotFoundException, SQLException{
		//iterate on each folders inside the folder
		 File[] files = new File(folderpath).listFiles();
		 
		 wordClasses= new String[NUM_CLASSES];
		 int classes[]= new int[NUM_CLASSES];
		 int i=0;
		 for (File file : files) {
			 if (file.isDirectory()) {
				
				//search in the database
				wordClasses[i]= file.getName();
				classes[i]= db.getIndex(wordClasses[i]);
				File[] wordFolder= file.listFiles();
				for (File wordImage : wordFolder) {
					Shorthand word= new Shorthand(wordImage, wordClasses[i], classes[i]);	
					testingSamples.add(word);
				}
				 
				i++;
			 }
		}
	}
	
	private void readImages(String folderpath){
		String test="";
		int offset=0;
		//get words from db
		String sql = "SELECT word, id FROM word LIMIT "+NUM_CLASSES+ " OFFSET "+offset; 
		wordClasses= new String[NUM_CLASSES];
		int classes[]= new int[NUM_CLASSES];
		int index=0;
        try {
			ResultSet rs= db.select(sql);
			while(rs.next()){
				wordClasses[index]=rs.getString("word");
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
		for(int j = 0 ; j < wordClasses.length ; j++){
			System.out.println(j);
			int files = new File(folderpath+"\\" +wordClasses[j]).listFiles().length;
			for(int k=1; k <= files; k++){
				File file= new File(folderpath+"\\" +wordClasses[j]+"\\"+"word ("+k+").jpg");
				Shorthand word= new Shorthand(file, wordClasses[j], classes[j]);
				testingSamples.add(word);
			}
		}
	}
	
	/**
	 * recognize each Shorthand samples in words (arraylist) 
	 * using the WordRecognizerClass
	 * 
	 */
	private void recognizeTestingData() throws ClassNotFoundException, IOException, SQLException{
		for(int i=0;i<testingSamples.size();i++){
			try {
				Shorthand word=testingSamples.get(i);
				r.recognize(word);
				System.out.println(word.annRes +" "+word.svmRes+" "+ word.bnRes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void deleteTableRows(){
		DefaultTableModel dm = (DefaultTableModel)resultsTable.getModel();
		int rowCount = dm.getRowCount();
		//Remove rows one by one from the end of the table
		for (int i = rowCount - 1; i >= 0; i--) {
		    dm.removeRow(i);
		}
	}
	/**
	 * displays results of testing samples in a table
	 * with rows: inputImage, actual, ANN classification,  SVM classification, BN classification, 
	 */
	private void displayResults(){
		for(int i=0;i<testingSamples.size();i++){
			Shorthand word= testingSamples.get(i);
			Image image;
			try {
				image = ImageIO.read(word.file);
				ImageIcon imageIcon = new ImageIcon(image);
				Object[] row= {imageIcon,word.word,word.annRes ,word.svmRes, word.bnRes};
				DefaultTableModel model = (DefaultTableModel)resultsTable.getModel();
				model.addRow(row);	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	public void displayAccuracy(TestingResult res){
		for(int i=0;i<wordClasses.length;i++){
			Object[] row= {wordClasses[i],res.precisionANN[i],res.recallANN[i] ,res.precisionSVM[i],res.recallSVM[i] ,res.precisionBN[i],res.recallBN[i] };
			DefaultTableModel model = (DefaultTableModel)accuracyTable.getModel();
			model.addRow(row);	
		}
		summaryTable.setValueAt(res.accuracyANN, 0, 1);
		summaryTable.setValueAt(res.accuracySVM, 0, 2);
		summaryTable.setValueAt(res.accuracyBN, 0, 3);
		
		summaryTable.setValueAt(res.overallPresicionANN, 1, 1);
		summaryTable.setValueAt(res.overallPresicionSVM, 1, 2);
		summaryTable.setValueAt(res.overallPresicionBN, 1, 3);
		
		summaryTable.setValueAt(res.overallRecallANN, 2, 1);
		summaryTable.setValueAt(res.overallRecallSVM, 2, 2);
		summaryTable.setValueAt(res.overallRecallBN, 2, 3);
		//System.out.println("results: \nANN:"+res.overallPresicionANN+"\nSVM"+res.overallPresicionSVM+"\nBN"+res.overallPresicionBN);
		
	}
	/**
	 * fills 'cTable' with the values in confMatrix
	 * 
	 * @param confMatrix  Mat - a result from TestingResult 'res'
	 */
	public void fillConfTable(Mat confMatrix){
		//initialize table header
		JTableHeader header= cTable.getTableHeader();
		for(int i=0;i<cTable.getColumnCount()-1;i++){
			TableColumn column1 = cTable.getTableHeader().getColumnModel().getColumn(i+1);
			column1.setHeaderValue(wordClasses[i]);
		}
		System.out.println(cTable.getColumnCount());
		//fill the first column with classes
		
		for(int i=0;i<wordClasses.length;i++){
			for(int j=0;j< wordClasses.length;j++){
				cTable.setValueAt(wordClasses[i], i, 0); 
				cTable.setValueAt(confMatrix.get(i, j)[0],i,j+1);
				
			}
		}
		
	}
	/**
	 * fills 'cTable' with the values in confMatrix
	 * 
	 * @param confMatrix  Mat - a result from TestingResult 'res'
	 */
	public void fillConfTable(double[][] confMatrix){
		//initialize table header
		JTableHeader header= cTable.getTableHeader();
		for(int i=0;i<cTable.getColumnCount()-1;i++){
			TableColumn column1 = cTable.getTableHeader().getColumnModel().getColumn(i+1);
			column1.setHeaderValue(wordClasses[i]);
		}
		System.out.println(cTable.getColumnCount());
		//fill the first column with classes
		
		for(int i=0;i<wordClasses.length;i++){
			for(int j=0;j< wordClasses.length;j++){
				cTable.setValueAt(wordClasses[i], i, 0); 
				cTable.setValueAt(confMatrix[i][j],i,j+1);
				
			}
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()== backButton){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Menu");
		}
		
		//browse for source folder in preprocessing
		else if(e.getSource()==selectFolder1){
			int a= srcFolder.showOpenDialog(this);
			if (a == JFileChooser.APPROVE_OPTION) {
				File file = srcFolder.getSelectedFile();
				folder1.setText(file.getAbsolutePath());
			}
		}
		//browse for destination folder in preprocessing
		else if(e.getSource()==selectDest){
			int a= destFolder.showOpenDialog(this);
			if (a == JFileChooser.APPROVE_OPTION) {
				File file = destFolder.getSelectedFile();
				destination.setText(file.getAbsolutePath());
			}
		}
		//browse for source folder in train/test
		else if(e.getSource()==selectFolder2){
			int a= tFolder.showOpenDialog(this);
			if (a == JFileChooser.APPROVE_OPTION) {
				File file = tFolder.getSelectedFile();
				folder2.setText(file.getAbsolutePath());
			}
		}
		//run test/train
		else if(e.getSource()==run){
			String path= folder2.getText();
			if(train.isSelected()){
			
				//reset graph and summary table
				resetResults();
				TrainTask trainTask= new TrainTask(this, path);
				//task.addPropertyChangeListener(this);
				trainTask.execute();
				run.setEnabled(false);

			}
			else if(test.isSelected()){
				TestTask testTask= new TestTask(this, path);
				testTask.execute();
				run.setEnabled(false);
		
			}
			else if(eval.isSelected()){
				//read the images
				//p.getAllFeatures2(path, "cross_validation_data");
				task = new Task(this);
				//task.addPropertyChangeListener(this);
				task.execute();
				run.setEnabled(false);

				
			}
		}
		//crop datasets
		 else if(e.getSource()==crop){
			 String sourcePath = folder1.getText().replace("\\", "/")+"/";
			 String destinationPath= destination.getText().replace("\\", "/")+"/";
			 p.cropImages(setList.getSelectedIndex()+1,sourcePath, destinationPath);
		 }
		 else if(e.getSource()== viewResults){
			 resultsDialog.setVisible(true);
		 }
		 else if(e.getSource()== viewConfButton){
			 d.setVisible(true);
		 }
		
		//show confusion matrix
		 else if(e.getSource()== viewANN){
			 //change color of button
			 viewANN.setColor("orange");
			 viewSVM.setColor("blue");
			 viewBN.setColor("blue");
			 
			 this.fillConfTable(evalANN.confusionMatrix());
		 }
		 else if(e.getSource()== viewSVM){
			 
			 viewSVM.setColor("orange");
			 viewBN.setColor("blue");
			 viewANN.setColor("blue");
			 this.fillConfTable(evalSVM.confusionMatrix());
		 }
		 else if(e.getSource()== viewBN){
			 viewBN.setColor("orange");
			 viewANN.setColor("blue");
			 viewSVM.setColor("blue");
			 this.fillConfTable(evalNB.confusionMatrix());
		 }
	}
	private void resetResults() {
		// TODO Auto-generated method stub
		//reset value in summary table
		for(int i=1;i<4;i++){
			summaryTable.setValueAt(0, 0, i);
			summaryTable.setValueAt(0, 1, i);
		}
		Platform.runLater(new Runnable(){
			@Override
			public void run() {	
				bc.getData().get(0).getData().get(0).setYValue(0);
				bc.getData().get(0).getData().get(1).setYValue(0);
				bc.getData().get(0).getData().get(2).setYValue(0);      
			
			}
		});
		
	}
	class Task extends SwingWorker<Void, String> {
		 TestingPanel panel;
		 public Task(TestingPanel panel){
			 this.panel = panel;
		 }
		 @Override
		 public Void doInBackground() {
			BufferedReader reader;
			try {
				publish("Reading data...");
				
				reader = new BufferedReader(new FileReader("cross_validation_data.arff"));
				final Instances trainingdata = new Instances(reader);
				reader.close();
				// setting class attribute
				trainingdata.setClassIndex(13);
				trainingdata.randomize(new Random(1));
				long startTime = System.nanoTime();

				publish("Training Naive Bayes Classifier...");
					
				NaiveBayes nb = new NaiveBayes();
				startTime = System.nanoTime();
				nb.buildClassifier(trainingdata);
				double runningTimeNB = (System.nanoTime() - startTime)/1000000;
				runningTimeNB /= 1000;
				//saving the naive bayes model 
				weka.core.SerializationHelper.write("naivebayes.model", nb);
				System.out.println("running time"+runningTimeNB);
				publish("Done training NB.\nEvaluating NB using 10-fold cross-validation...");
				evalNB = new Evaluation(trainingdata);
				evalNB.crossValidateModel(nb, trainingdata, 10, new Random(1));
				publish("Done evaluating NB.");
					
				//System.out.println(evalNB.toSummaryString("\nResults for Naive Bayes\n======\n", false)); 
					
				MultilayerPerceptron mlp = new MultilayerPerceptron();
				mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a")); 
				publish("Training ANN...");
				startTime = System.nanoTime();
				mlp.buildClassifier(trainingdata);
				long runningTimeANN = (System.nanoTime() - startTime)/1000000;
				runningTimeANN /= 1000;
				//saving the MLP model
				weka.core.SerializationHelper.write("mlp.model", mlp);

				publish("Done training ANN.\nEvaluating ANN using 10-fold cross-validation...");
				
				evalANN = new Evaluation(trainingdata);
				evalANN.evaluateModel(mlp, trainingdata);
				//evalMLP.crossValidateModel(mlp, trainingdata, 10, new Random(1));
				
				publish("Done evaluating ANN.");
				publish("Training SVM...");
				SMO svm = new SMO();
					
				startTime = System.nanoTime();
				svm.buildClassifier(trainingdata);
				long runningTimeSVM = (System.nanoTime() - startTime)/1000000;
				runningTimeSVM /= 1000;
				weka.core.SerializationHelper.write("svm.model", svm);
				publish("Done training SVM.\nEvaluating SVM using 10-fold cross-validation...");
				evalSVM = new Evaluation(trainingdata);
				evalSVM.evaluateModel(svm, trainingdata);
				publish("Done evaluating SVM.");
					
				Platform.runLater(new Runnable(){
					@Override
					public void run() {	
						bc.getData().get(0).getData().get(0).setYValue(evalANN.correct()/trainingdata.size()*100);
						bc.getData().get(0).getData().get(1).setYValue(evalSVM.correct()/trainingdata.size()*100);
						bc.getData().get(0).getData().get(2).setYValue(evalNB.correct()/trainingdata.size()*100);      
					
						for(int i=0;i<NUM_CLASSES;i++){
							lineChart.getData().get(0).getData().get(i).setYValue(evalANN.recall(i)*100);
							lineChart.getData().get(1).getData().get(i).setYValue(evalSVM.recall(i)*100);
							lineChart.getData().get(2).getData().get(i).setYValue(evalNB.recall(i)*100);
							
						}
					
					}
				});
				
				panel.fillConfTable(evalSVM.confusionMatrix());
				
				summaryTable.setValueAt(evalANN.correct()/trainingdata.size()*100., 0, 1);
				summaryTable.setValueAt(evalSVM.correct()/trainingdata.size()*100, 0, 2);
				summaryTable.setValueAt(evalNB.correct()/trainingdata.size()*100,0,3);
				
				summaryTable.setValueAt(runningTimeANN, 1, 1);
				summaryTable.setValueAt(runningTimeSVM, 1, 2);
				summaryTable.setValueAt(runningTimeNB,1,3);
				
				
				
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	return null;
		    }
		protected void process(List<String> chunks) {
			for (final String string : chunks) {
			     console.append(string+"\n");
			}	 
		};
		@Override
		public void done() {
		  Toolkit.getDefaultToolkit().beep();
		  run.setEnabled(true);
		  console.append("Done evaluating models.\n");
		}
	 }
	class TrainTask extends SwingWorker<Void, String> {
		 TestingPanel panel;
		 String path;
		 public TrainTask(TestingPanel panel,String path){
			 this.panel = panel;
			 this.path = path;
		 }
		 @Override
		 public Void doInBackground() {
			BufferedReader reader;
			publish("Computing features...");
			int trainingSamples=p.getAllFeatures2(path, "training_data");
			publish("Done computing features.");
			File folder= new File(path);
//			int classes= folder.listFiles().length;
			try {
				publish("Training models...");
				Training train= new Training(NUM_CLASSES, trainingSamples, this.panel);
				trainingTime =  train.getAllBuildTime();
				publish("Training time:");
				publish("ANN\t:  "+trainingTime[0]+" seconds");
				publish("SVM\t:  "+trainingTime[1]+" seconds");
				publish("NB\t:  "+trainingTime[2]+ " seconds");
				
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    	return null;
		    }
		protected void process(List<String> chunks) {
			for (final String string : chunks) {
			     console.append(string+"\n");
			}	 
		};
		@Override
		public void done() {
		  Toolkit.getDefaultToolkit().beep();
		  run.setEnabled(true);
		  console.append("Done training models.\n");
		}
	 }
	class TestTask extends SwingWorker<Void, String> {
		 TestingPanel panel;
		 String path;
		 public TestTask(TestingPanel panel,String path){
			 this.panel = panel;
			 this.path = path;
		 }
		 @Override
		 public Void doInBackground() {
			BufferedReader reader;
			publish("Computing features...");
			int testingSamples=p.getAllFeatures2(path, "testing_data");
		
			try {
				publish("Reading data...");
				
				reader = new BufferedReader(new FileReader("testing_data.arff"));
				final Instances testingdata = new Instances(reader);
				reader.close();
				// setting class attribute
				testingdata.setClassIndex(13);
				testingdata.randomize(new Random(1));
				long startTime = System.nanoTime();
				Classifier ann = (Classifier) weka.core.SerializationHelper.read("mlp.model");
				publish("Evaluating ANN...");
				
				
				evalANN = new Evaluation(testingdata);
				startTime= System.nanoTime();
				evalANN.evaluateModel(ann, testingdata);
				long runningTimeANN = (System.nanoTime() - startTime)/1000000;
				//runningTimeANN /= 100;
				
				publish("Done evaluating ANN");
				
				publish("Evaluating SVM...");
				Classifier svm = (Classifier) weka.core.SerializationHelper.read("svm.model");
				
				evalSVM = new Evaluation(testingdata);
				startTime= System.nanoTime();
				evalSVM.evaluateModel(svm, testingdata);
				long runningTimeSVM = (System.nanoTime() - startTime)/1000000;
				//runningTimeSVM /= 100;
				publish("Done evaluating SVM");
				
				
				
				publish("Evaluating NB...");
				Classifier nb = (Classifier) weka.core.SerializationHelper.read("naivebayes.model");
				
				evalNB = new Evaluation(testingdata);
				startTime = System.nanoTime();
				evalNB.evaluateModel(nb, testingdata);
				long runningTimeNB = (System.nanoTime() - startTime)/1000000;
				//runningTimeNB /= 100;
				publish("Done evaluating ANN");
				
				Platform.runLater(new Runnable(){
					@Override
					public void run() {	
						bc.getData().get(0).getData().get(0).setYValue(evalANN.correct()/testingdata.size()*100);
						bc.getData().get(0).getData().get(1).setYValue(evalSVM.correct()/testingdata.size()*100);
						bc.getData().get(0).getData().get(2).setYValue(evalNB.correct()/testingdata.size()*100);      
					
						for(int i=0;i<NUM_CLASSES;i++){
							lineChart.getData().get(0).getData().get(i).setYValue(evalANN.recall(i)*100);
							lineChart.getData().get(1).getData().get(i).setYValue(evalSVM.recall(i)*100);
							lineChart.getData().get(2).getData().get(i).setYValue(evalNB.recall(i)*100);
							
						}
					
					}
				});
				
				panel.fillConfTable(evalSVM.confusionMatrix());
				
				summaryTable.setValueAt(evalANN.correct()/testingdata.size()*100., 0, 1);
				summaryTable.setValueAt(evalSVM.correct()/testingdata.size()*100, 0, 2);
				summaryTable.setValueAt(evalNB.correct()/testingdata.size()*100,0,3);
				
				summaryTable.setValueAt(runningTimeANN, 1, 1);
				summaryTable.setValueAt(runningTimeSVM, 1, 2);
				summaryTable.setValueAt(runningTimeNB,1,3);
				
				
				
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	return null;
		
		    }
		protected void process(List<String> chunks) {
			for (final String string : chunks) {
			     console.append(string+"\n");
			}	 
		};
		@Override
		public void done() {
		  Toolkit.getDefaultToolkit().beep();
		  run.setEnabled(true);
		  console.append("Done training models.\n");
		}
	 }
}

