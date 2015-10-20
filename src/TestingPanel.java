import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.opencv.core.Mat;

class TestingPanel extends JPanel implements ActionListener{
	private static final int NUM_CLASSES=30;
	
	private ArrayList<Shorthand> testingSamples;
	private ClassyButton  selectFolder1,selectFolder2,selectDest, run,crop;
	private ClassyButton viewConfButton, viewANN, viewSVM, viewBN;
	private MainPanel1 card;
	private JButton backButton;
	private JComboBox setList;
	private JDialog d;
	private JFileChooser srcFolder, destFolder,tFolder;
	private JLabel title, heading, rLabel;
	private JLabel selectLabel1,selectLabel2, selectData;
	private JLabel heading2, heading3;
	private JLabel sLabel, dLabel;    //source and destination label
	private JPanel pPanel,tPanel;
	private JPanel navBar;
	private JPanel resultsPanel, predictedPanel, cPanel, aPanel;
	private JRadioButton train,test;
	private JTable resultsTable, cTable, accuracyTable, summaryTable;
	private JTextArea textArea;
	private JTextField folder1,folder2, destination;
	private Preprocessing p;
	private String[] wordClasses;
	private TestingResult res;
	private WordDB db;
	private WordRecognizer r;
	
	public TestingPanel(MainPanel1 card){
		this.setBackground(Color.white);
		this.setLayout(null);
		this.card= card;
		addHeaderComponents();
		addComponents();
		p= new Preprocessing(30);
		db= new WordDB();
		testingSamples= new ArrayList<Shorthand>();
		r= new WordRecognizer();
	}
	private void addComponents(){
		//preprocess panel components
		pPanel = new JPanel();
		pPanel.setBounds(20, 160, 300, 180);
		pPanel.setLayout(null);
		this.add(pPanel);
		
		heading2 = new JLabel("Preprocessing");
		heading2.setBounds(100,5,100,30);
		pPanel.add(heading2);
		
		selectData= new JLabel("Select Dataset:");
		selectData.setBounds(50,30,100,30);
		pPanel.add(selectData);
		String[] list= {"1","2","3","4"};
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
		tPanel.setBounds(20, 360, 300, 180);
		tPanel.setLayout(null);
		this.add(tPanel);
		
		train= new JRadioButton("Train");
		test= new JRadioButton("Test");
		
		ButtonGroup group = new ButtonGroup();
        group.add(train);
        group.add(test);
        JPanel radioPanel = new JPanel(new GridLayout(1, 0));
        radioPanel.add(train);
        radioPanel.add(test);
        
        radioPanel.setBounds(60, 30, 200, 30);
        tPanel.add(radioPanel);
        
        tFolder = new JFileChooser();
		tFolder.setCurrentDirectory(new java.io.File("."));
	    tFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		folder2= new JTextField(10);
		folder2.setBounds(30, 60, 130, 25);
		tPanel.add(folder2);
		
		selectFolder2 = new ClassyButton("Select Folder", "white");
		selectFolder2.setBounds(170, 60, 110, 25);
		tPanel.add(selectFolder2);
		
		run= new ClassyButton("Run", "blue");
		run.setBounds(100, 100, 100, 30);
		tPanel.add(run);
		
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
			String[] columnNames = {"Image","Acutal","ANN","SVM","BN"};
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
		cTable= new JTable(31,31){
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
		        return comp;
		    }
		};
		
		JScrollPane scroll2= new JScrollPane(cTable);
		
		scroll2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll2.setBounds(100, 100, 1000, 550);
		//cPanel.add(scroll2);
		viewANN= new ClassyButton("ANN", "blue");
		viewSVM= new ClassyButton("SVM", "orange");
		viewBN= new ClassyButton("BN", "blue");
		
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
		
		
		//create panel and table for accuracy results
		aPanel = new JPanel();
		aPanel.setLayout(null);
		String[] columnNames = {"Word","ANN Precision","ANN Recall","SVM Precision","SVM Recall","BN Precision","BN Recall"};
		Object[] row= {"","","" ,"", "","",""};
	    Object[][] data = {row};
	    TableModel model = new DefaultTableModel(data, columnNames);
	    accuracyTable = new JTable(model);
	    JScrollPane scroll3= new JScrollPane(accuracyTable);
	    scroll3.setBounds(5, 5, 380, 200);
	    aPanel.add(scroll3);
	    
	    viewConfButton= new ClassyButton("View Confusion Matrix", "blue");
	    viewConfButton.setBounds(200, 230, 200, 30);
	    viewConfButton.addActionListener(this);
	    aPanel.add(viewConfButton);
	    
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
		 
		 wordClasses= new String[files.length];
		 int classes[]= new int[files.length];
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
	private void recognizeTestingData(){
		for(int i=0;i<testingSamples.size();i++){
			try {
				Shorthand word=testingSamples.get(i);
				r.recognize(word);
				System.out.println(word.annRes +" "+word.svmRes+" "+ word.bnRes);
			} catch (ClassNotFoundException | IOException | SQLException e) {
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
				
				//this.readImages2(path);
				int trainingSamples=p.getAllFeatures2(path, "training_data");
				File folder= new File(path);
				int classes= folder.listFiles().length;

				Training train= new Training(classes, trainingSamples);

			}
			else if(test.isSelected()){
//				Preprocessing p= new Preprocessing(30);
//				int testingSamples= p.getAllFeatures(path, "testing_data");
//				System.out.println(testingSamples);
//				ML test = new ML( testingSamples, 30);
				
				
				try {
					this.readImages2(path);
					System.out.println(Arrays.toString(wordClasses));

					this.recognizeTestingData();
					res= new TestingResult(wordClasses, testingSamples);
					
					//deletes data in resultsTable
					this.deleteTableRows();
					
					//display results in table
					this.displayResults();
					this.displayAccuracy(res);
					cTable.setModel(new DefaultTableModel(wordClasses.length,wordClasses.length+1));
					Mat svmConf= res.getConfusinMatrix("svm");
					this.fillConfTable(svmConf);
				
				} catch (ClassNotFoundException | SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
//				//previous do not edit
//				//read images
//				this.readImages(path);
//				this.recognizeTestingData();
//				res= new TestingResult(wordClasses, testingSamples);
//				
//				//deletes data in resultsTable
//				this.deleteTableRows();
//				
//				//display results in table
//				this.displayResults();
//				this.displayAccuracy(res);
//				Mat svmConf= res.getConfusinMatrix("svm");
//				this.fillConfTable(svmConf);
//				
			}
		}
		//crop datasets
		 else if(e.getSource()==crop){
			 String sourcePath = folder1.getText().replace("\\", "/")+"/";
			 String destinationPath= destination.getText().replace("\\", "/")+"/";
			 p.cropImages(setList.getSelectedIndex()+1,sourcePath, destinationPath);
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
			 
			 this.fillConfTable(res.getConfusinMatrix("ann"));
		 }
		 else if(e.getSource()== viewSVM){
			 
			 viewSVM.setColor("orange");
			 viewBN.setColor("blue");
			 viewANN.setColor("blue");
			 this.fillConfTable(res.getConfusinMatrix("svm"));
		 }
		 else if(e.getSource()== viewBN){
			 viewBN.setColor("orange");
			 viewANN.setColor("blue");
			 viewSVM.setColor("blue");
			 this.fillConfTable(res.getConfusinMatrix("bn"));
		 }
	}

}

