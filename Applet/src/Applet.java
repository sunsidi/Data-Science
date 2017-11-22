import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class Applet extends JApplet implements ActionListener {
	
	String[] members = {"Sidi", "Nikita"};
	ButtonGroup roleButtons = new ButtonGroup();
	JButton submit = new JButton("Create Signature");
	JButton open = new JButton("Open File");
	JPanel[] p = new JPanel[5];
	JRadioButton[] radios = new JRadioButton[2];
	JPasswordField pwd = new JPasswordField(20);
	JFileChooser fc = new JFileChooser();

	public void init() {
		getContentPane().setLayout(new GridLayout(5, 1));
		for (int i=0; i<5; i++) {
			p[i] = new JPanel();
			if ( i != 0 ) p[i].setLayout(new FlowLayout(FlowLayout.LEFT));
			if ( i == 4 ) p[i].setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(p[i]); 
		}
		
		// add even listeners on the buttons
		pwd.addActionListener(this);
		submit.addActionListener(this);
		open.addActionListener(this);

		// title
		p[0].add(new JLabel("Create Digital Signature"));
		// add upload label and upload selections
		p[1].add(new JLabel("Select Message "));
		p[1].add(open);
		// add member selection label and radio buttons
		p[2].add(new JLabel("Select Member "));

		for (int i=0; i<radios.length; i++) {
			radios[i] = new JRadioButton(members[i]);
			radios[i].addActionListener(this);
			roleButtons.add(radios[i]);
			p[2].add(radios[i]); 
		}
		// add pass phrase label and text filed
		p[3].add(new JLabel("Passphrase "));
		p[3].add(pwd);

		// add the submit button
		p[4].add(submit);
	}
	public void actionPerformed(ActionEvent e) {

		//Handle upload action.
		if (e.getSource() == open) {
			int returnVal = fc.showOpenDialog(Applet.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

			} else {

			}
		}
		//Handle create signature action
		else if (e.getSource() == submit) {
			
		}
		
	}
}	
