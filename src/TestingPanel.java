import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class TestingPanel extends JPanel implements ActionListener{
	private MainPanel1 card;
	private JLabel title, heading, rLabel;
	private JLabel selectLabel1,selectLabel2, selectData;
	private JLabel heading2, heading3;
	private JLabel sLabel, dLabel;    //source and destination label
	private JPanel pPanel,tPanel;
	private ClassyButton  selectFolder1,selectFolder2,selectDest, run,crop;
	private JTextArea textArea;
	private JPanel navBar;
	private JButton backButton;
	private JTextField folder1,folder2, destination;
	private JRadioButton train,test;
	private JComboBox setList;
	private JFileChooser srcFolder, destFolder,tFolder;
	private Preprocessing p;
	public TestingPanel(MainPanel1 card){
		this.setBackground(Color.white);
		this.setLayout(null);
		this.card= card;
		addHeaderComponents();
		addComponents();
		p= new Preprocessing();
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
				
	        	int trainingSamples=p.getAllFeatures(path, "training_data");
	 	        ML train= new ML(trainingSamples, 30, true);
			}
			else if(test.isSelected()){
				Preprocessing p= new Preprocessing(30);
				int testingSamples= p.getAllFeatures(path, "testing_data");
				System.out.println(testingSamples);
				ML test = new ML( testingSamples, 30);
			}
		}
		//crop datasets
		 else if(e.getSource()==crop){
			 String sourcePath = folder1.getText().replace("\\", "/")+"/";
			 String destinationPath= destination.getText().replace("\\", "/")+"/";
			 p.cropImages(setList.getSelectedIndex()+1,sourcePath, destinationPath);
		 }
	}
}
