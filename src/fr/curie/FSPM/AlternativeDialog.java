package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE  
*/
import java.awt.Dimension;
import java.awt.GridBagConstraints;
/**
 * A dialog box for choosing one option
 * among two by radio button
 * 
 * @author Daniel.Rovera@curie.fr
 */
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
public class AlternativeDialog extends GridBagDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	final static int cx[]={0,0,0,0,1};
	final static int cy[]={0,1,2,3,3};
	final static int cw[]={2,2,2,1,1};
	final static int ch[]={1,1,1,1,1};
	final static int xw[]={0,0,0,0,0};
	final static int yw[]={0,0,0,0,0} ;
	final static int cf[]={B,B,B,B,B};
	final int width=240;
	final int height=160;
	private JRadioButton rb1, rb2;
	private int option;
	private JButton okBouton, cancelBouton ;
	public AlternativeDialog(JFrame parent,String title,String label,String rb1Text,String rb2Text) {
		super(parent,title,true,cx,cy,cw,ch,xw,yw,cf);
		setSize(width,height);
		container=getContentPane();
		container.setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		addWithConstraints(0,new JLabel(label,JLabel.CENTER));
		ButtonGroup group = new ButtonGroup() ;
		rb1=new JRadioButton(rb1Text,true);
		group.add(rb1);
		addWithConstraints(1,rb1);
		rb1.addActionListener(this);
		rb2=new JRadioButton (rb2Text,false);
		group.add(rb2);
		addWithConstraints(2,rb2);
		rb2.addActionListener(this);
		okBouton = new JButton ("OK") ;
		addWithConstraints(3,okBouton);		
		okBouton.addActionListener(this) ;
		cancelBouton = new JButton ("Cancel") ;
		addWithConstraints(4,cancelBouton) ;
		cancelBouton.addActionListener(this);		
		setSize(280, 150);		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - getHeight()) / 2);
	    setLocation(x, y);		
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});	
	}
	public void actionPerformed(ActionEvent e){		
		if (e.getSource()==okBouton){
			if (rb1.isSelected()) option=1;
			if (rb2.isSelected()) option=2;
			dispose();
		}
		if (e.getSource()==cancelBouton) dispose();	
	}
	public int getOption(){
		option=0;
		setVisible(true);				
		return option;
	}
}
