import java.awt.Color;

import javax.swing.JButton;

public class ClassyButton extends JButton {
	String color;
	public ClassyButton(String label, String color){
		super(label);
		this.color= color;
		setColor(this.color);
		this.setFocusPainted(false);
		setBorderPainted(false);
		setRolloverEnabled(true);
	    
	}
	void setColor(String color){
		
		this.setForeground(Color.white);
		switch(color){
			case "blue": this.setBackground(new Color(21, 136, 230)); break;
			case "orange": this.setBackground(Color.ORANGE); break;
			case "white": this.setBackground(Color.WHITE);
							this.setForeground(Color.black);break;
				
		}
	}
}
