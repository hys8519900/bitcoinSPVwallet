package foo;


import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;

public class TxDialog extends JDialog{
	public TxDialog(Frame owner, String title, boolean modal)
	{
		super(owner, title, modal);
		//setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setLocationRelativeTo(null);
		
	}
}
