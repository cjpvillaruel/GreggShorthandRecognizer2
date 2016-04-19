import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

class WordRecognizerPanel extends JPanel implements ActionListener{
	private MainPanel1 card;
	private JLabel title, heading, rLabel;
	private JLabel numLabel, imageLabel;
	private JPanel navBar,viewAllPanel;
	private JButton backButton;
	private JPanel imagePanel;
	private ClassyButton recognize, next, prev, viewAll,select;
	private JTable table, table2;
	private FileDialog fd;
	private File[] files;
	private int index;
	private WordRecognizer recognizer;
	private Shorthand[] words;
	private Boolean isRecognized;
	private WordDB db;
	private JDialog d1;
	public WordRecognizerPanel(MainPanel1 card){
		this.setBackground(Color.white);
		this.card= card;
		this.db= new WordDB();
		this.isRecognized=false;
		this.setLayout(null);
		this.addHeaderComponents();
		this.addComponents();
		
		fd = new FileDialog(new JFrame(), "Choose a file", FileDialog.LOAD);
		fd.setMultipleMode(true);
		fd.setDirectory(".");
		
		recognizer= new WordRecognizer();
		
	}
	private void addComponents(){
		
		
		imagePanel = new JPanel();
		imagePanel.setBounds(50, 230, 250, 180);
		imagePanel.setBackground(new Color(28, 187,180));
		this.add(imagePanel);
		
		imageLabel = new JLabel();
		imagePanel.add(imageLabel);
		
		select= new ClassyButton("Select image/s", "white");
		select.addActionListener(this);
		imagePanel.add(select);
		
		numLabel= new JLabel("0 of 0 images");
		numLabel.setBounds(140, 430, 100, 30);
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
	    
	    
	    viewAllPanel= new JPanel();
	    
	    String[] columnNames2 = {"Image","ANN","SVM","BN"};
	    Image image;
		try {
			image = ImageIO.read(new File("images/data2/result_0.jpg"));
			ImageIcon imageIcon = new ImageIcon(image);
			Object[] row= {imageIcon,"" ,"", ""};
		    Object[][] data2 = {row};
		    model = new DefaultTableModel(data2, columnNames2);
		    table2 = new JTable(model){
	            //  Returning the Class of each column will allow different
	            //  renderers to be used based on Class
	            public Class getColumnClass(int column)
	            {
	                return getValueAt(0, column).getClass();
	            }
	        };
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		table2.setRowHeight(100);
	    JScrollPane scroll2 = new JScrollPane (table2);
	    viewAllPanel.add(scroll2);
	    d1=new JDialog(new JFrame(),"Results",true);
	    d1.setSize(600, 500);
	    d1.add(viewAllPanel);
	    d1.setLocationRelativeTo(null);
		d1.setResizable(false);
		
		
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
	
	
	private void initializeWords(){
		words= new Shorthand[files.length];
		for(int i=0;i<files.length;i++){
			words[i]= new Shorthand(files[i]);
		}
	}
	private void displayAllResults() throws IOException{
		
		for(int i=0;i<words.length;i++){
			
			Image image = ImageIO.read(files[i]);
	    	ImageIcon imageIcon = new ImageIcon(image);
			Object[] row= {imageIcon,words[i].annRes ,words[i].svmRes, words[i].bnRes};
			DefaultTableModel model = (DefaultTableModel)table2.getModel();
			model.addRow(row);	
		}
	}
	private void displayResult(){
		if(isRecognized){
			table.setValueAt(words[index].annRes, 0,0 );
			table.setValueAt(words[index].svmRes, 0,1 );
			table.setValueAt(words[index].bnRes, 0,2);
		}
	}
	/**
	 * 
	 * @param index int index of the current file
	 * 
	 * Updates the image and the label in the panel
	 */
	private void updateImage(int index){
		numLabel.setText((index+1)+" of "+ files.length+" images");
		try {                
	    	Image image = ImageIO.read(files[index]);
	    	ImageIcon imageIcon = new ImageIcon(image);
	        imageLabel.setIcon(imageIcon);
	        imageLabel.setText("");
	        //path= file.getPath();
	    } catch (IOException ex) {
	    }
	}
	private void deleteTableRows(){
		DefaultTableModel dm = (DefaultTableModel)table2.getModel();
		int rowCount = dm.getRowCount();
		//Remove rows one by one from the end of the table
		for (int i = rowCount - 1; i >= 0; i--) {
		    dm.removeRow(i);
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()== backButton){
			CardLayout cl = (CardLayout)(this.card.getLayout());
	        cl.show(this.card, "Menu");
		}
		else if(e.getSource()== select){
			fd.setVisible(true);
			files = fd.getFiles();
			if(files.length>0){
				index=0;
				updateImage(index);
				this.initializeWords();
				this.isRecognized=false;
			}
			else{
				//if cancel is selected
				numLabel.setText("0 of 0 images");
				index=0;
				imageLabel.setIcon(null);
			}
			
			//reset table
			table.setValueAt("", 0,0 );
			table.setValueAt("", 0,1 );
			table.setValueAt("", 0,2);
			viewAll.setEnabled(false);
			
		}
		else if(e.getSource()== recognize){
			if(files.length != 0){
				//recognize all
				for(int i=0;i<files.length;i++){
					try {
						recognizer.recognize(words[i]);
						this.isRecognized=true;
						displayResult();		
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				viewAll.setEnabled(true);
			}
		}
		else if(e.getSource()== prev){
			
			if(index>0){
				index--;
				updateImage(index);
				displayResult();
					
			}
		}
		else if(e.getSource()== next){
			
			if(index<files.length -1 ){
				index++;
				updateImage(index);
				displayResult();
			}
		}
		else if(e.getSource()== viewAll){
			deleteTableRows();
			try {
				this.displayAllResults();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			d1.setVisible(true);
			
		}
		 
	}
}