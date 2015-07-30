import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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