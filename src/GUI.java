import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
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
import java.sql.SQLException;

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


