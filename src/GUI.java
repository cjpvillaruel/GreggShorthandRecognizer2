import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import java.awt.BorderLayout;
import java.awt.CardLayout;

public class GUI extends JFrame {
	MainPanel1 m;
	public GUI() {
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
		m = new MainPanel1();
		Container c = this.getContentPane();
		c.add(m);
		
	}
	
	public static void main(String[] args){
		GUI gui= new GUI();
	}

}
class MainPanel1 extends JPanel{
	Menu menuPanel;
	DocumentReaderPanel dPanel;
	WordRecognizerPanel wPanel;
	TestingPanel tPanel;
	public MainPanel1(){
		this.setLayout(new CardLayout());
		dPanel= new DocumentReaderPanel(this);
		wPanel= new WordRecognizerPanel(this);
		tPanel= new TestingPanel(this);
		menuPanel= new Menu(this);
		
		this.add(menuPanel,"Menu");
		this.add(dPanel,"Document");
		this.add(tPanel,"Testing");
		this.add(wPanel,"Word");
		
		CardLayout cl = (CardLayout)(this.getLayout());
        cl.show(this, "Menu");
	}
}
class Menu extends JPanel implements ActionListener, MouseListener{
	private ClassyButton testButton, wordButton, documentButton;
	private JLabel testIcon, wordIcon, documentIcon;
	private MainPanel1 card;
	private Image docuImage, testImage, wordImage;
	private ImageIcon icon;
	private JLabel title;
	public Menu(MainPanel1 card){
		this.addComponents();
		this.setBackground(Color.white);
		this.card= card;
		this.setLayout(null);
	}
	private void addComponents(){
		this.title= new JLabel("Gregg Shorthand Recognizer");
		this.add(title);
		this.title.setBounds(250, 50, 300, 30);
		this.title.setFont(new Font("Sans Serif", Font.BOLD, 20));
		
		
		this.readIcons();
    	icon = new ImageIcon(testImage);
    	testIcon= new JLabel();
        testIcon.setIcon(icon);
        testIcon.setBounds(60, 180, 180, 180);
        testIcon.addMouseListener(this);
        this.add(testIcon);
        
        
    	icon = new ImageIcon(wordImage);
    	wordIcon= new JLabel();
        wordIcon.setIcon(icon);
        wordIcon.addMouseListener(this);
        wordIcon.setBounds(300, 180, 180, 180);
        this.add(wordIcon);
        
        
    	ImageIcon icon = new ImageIcon(docuImage);
    	documentIcon= new JLabel();
        documentIcon.setIcon(icon);
        documentIcon.addMouseListener(this);
        documentIcon.setBounds(550, 180, 180, 180);
        this.add(documentIcon);
        
        this.testButton= new ClassyButton("Test Accuracy","blue");
		this.testButton.setBounds(60, 380, 180, 30);
		this.add(testButton);
		testButton.addActionListener(this);
		
		this.wordButton=  new ClassyButton("Word Recognizer","blue");
		this.wordButton.setBounds(300, 380, 180, 30);
		this.add(wordButton);
		wordButton.addActionListener(this);
		
		this.documentButton=  new ClassyButton("Document Reader","blue");
		this.documentButton.setBounds(550, 380, 180, 30);
		this.add(documentButton);
		documentButton.addActionListener(this);
	}
	

	void readIcons(){
		try {
			testImage = ImageIO.read(new File("images/ui/testing.png"));
			docuImage = ImageIO.read(new File("images/ui/documentreader.png"));
			wordImage = ImageIO.read(new File("images/ui/word.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()== testButton){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Testing");
		}
		else if(e.getSource()== wordButton){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Word");
		}
		else if(e.getSource()== documentButton){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Document");
		}
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==testIcon){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Testing");
		}
		if(e.getSource()==documentIcon){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Document");
	      
		}
		if(e.getSource()==wordIcon){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Word");
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
class WordRecognizerPanel extends JPanel implements ActionListener{
	private MainPanel1 card;
	private JLabel title, heading, rLabel;
	private JLabel numLabel;
	private JPanel navBar;
	private JButton backButton;
	private JPanel imagePanel;
	private ClassyButton recognize, next, prev, viewAll,select;
	private JTable table;
	
	public WordRecognizerPanel(MainPanel1 card){
		this.setBackground(Color.white);
		this.card= card;
		
		this.setLayout(null);
		this.addHeaderComponents();
		this.addComponents();
		
	}
	private void addComponents(){
		imagePanel = new JPanel();
		imagePanel.setBounds(50, 230, 250, 180);
		imagePanel.setBackground(new Color(28, 187,180));
		this.add(imagePanel);
		
		select= new ClassyButton("Select image/s", "white");
		select.addActionListener(this);
		imagePanel.add(select);
		
		numLabel= new JLabel("0 of 0 images");
		numLabel.setBounds(135, 430, 100, 30);
		this.add(numLabel);
		
		next= new ClassyButton("next","orange");
		next.setBounds(240, 430, 60, 30);
		next.addActionListener(this);
		this.add(next);
		
		prev= new ClassyButton("prev","orange");
		prev.setBounds(50, 430, 60, 30);
		prev.addActionListener(this);
		this.add(prev);
		
		recognize= new ClassyButton("Recognize", "blue");
		recognize.setBounds(320, 300, 150, 30);
		recognize.addActionListener(this);
		this.add(recognize);
		
		String[] columnNames = {"ANN",
                "SVM",
                "BN",
               };
	    Object[][] data = {{"","",""}};
	    TableModel model = new DefaultTableModel(data, columnNames);
	    table = new JTable(model);
	
	    JScrollPane scroll = new JScrollPane (table);
		scroll.setBounds(500, 280, 270, 50);
		this.add(scroll);
		JTableHeader header = table.getTableHeader();
	    header.setBackground(Color.orange);
	    table.setRowHeight(0, 30);
	    
	    viewAll= new ClassyButton("View All Results", "blue");
	    viewAll.setBounds(600, 350, 150, 30);
	    viewAll.setEnabled(false);
	    viewAll.addActionListener(this);
	    this.add(viewAll);
	}
	
	private void addHeaderComponents(){
		this.title= new JLabel("Gregg Shorthand Recognizer");
		this.title.setBounds(250, 50, 300, 30);
		this.title.setFont(new Font("Sans Serif", Font.BOLD, 20));
		this.add(title);
		
		this.heading= new JLabel("Word Recognizer");
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
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()== backButton){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Menu");
		}
	}
}
class DocumentReaderPanel extends JPanel implements ActionListener{
	private MainPanel1 card;
	private JLabel title, heading, rLabel;
	private JPanel imagePanel;
	private ClassyButton rButton, selectImage;
	private JTextArea textArea;
	private JPanel navBar;
	private JButton backButton;
	
	public DocumentReaderPanel(MainPanel1 card){
		this.setBackground(Color.WHITE);
		this.card= card;
		this.setLayout(null);
		
		this.title= new JLabel("Gregg Shorthand Recognizer");
		this.title.setBounds(250, 50, 300, 30);
		this.title.setFont(new Font("Sans Serif", Font.BOLD, 20));
		this.add(title);		
		
		
		this.heading= new JLabel("Document Reader");
		this.heading.setBounds(120, 125, 200, 30);
		this.heading.setFont(new Font("Sans Serif", Font.BOLD, 16));
		this.heading.setForeground(Color.WHITE);
		this.add(heading);
		
		
		imagePanel= new JPanel();
		imagePanel.setBounds(50, 180, 330, 380);
		imagePanel.setBackground(Color.orange);
		this.add(imagePanel);
		
		selectImage= new ClassyButton("Select image", "white");
		imagePanel.add(selectImage);
		
		rButton= new ClassyButton("Translate","blue");
		rButton.setBounds(400, 300, 100, 30);
		this.add(rButton);
		
		textArea= new JTextArea();
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane (textArea);
		scroll.setBounds(530, 250, 230, 130);
		this.add(scroll);
		
		rLabel= new JLabel("Result:");
		rLabel.setBounds(530, 200, 200, 30);
		this.add(rLabel);
		
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
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()== rButton){
			//
		}
		else if(e.getSource()== backButton){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Menu");
		}
	}
}
class TestingPanel extends JPanel implements ActionListener{
	private MainPanel1 card;
	private JLabel title, heading, rLabel;
	private JLabel selectLabel1,selectLabel2, selectData;
	private JLabel heading2, heading3;
	private JLabel sLabel, dLabel;    //source and destination label
	private JPanel pPanel,tPanel;
	private ClassyButton rButton, selectFolder1,selectFolder2,selectDest, run,crop;
	private JTextArea textArea;
	private JPanel navBar;
	private JButton backButton;
	private JTextField folder1,folder2, destination;
	private JRadioButton train,test;
	private JComboBox setList;
	public TestingPanel(MainPanel1 card){
		this.setBackground(Color.white);
		this.setLayout(null);
		this.card= card;
		addHeaderComponents();
		addComponents();
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
		
		folder2= new JTextField(10);
		folder2.setBounds(30, 60, 130, 25);
		tPanel.add(folder2);
		
		selectFolder2 = new ClassyButton("Select Folder", "white");
		selectFolder2.setBounds(170, 60, 110, 25);
		tPanel.add(selectFolder2);
		
		run= new ClassyButton("Run", "blue");
		run.setBounds(100, 100, 100, 30);
		tPanel.add(run);
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
		//java.net.URL imgURL = getClass().getResource("../images/ui/word.png");
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
	}
}
