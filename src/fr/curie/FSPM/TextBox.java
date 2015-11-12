package fr.curie.FSPM;
/*
   Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
   Copyright (C) 2010-2011 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * Not modal dialog displaying text and allowing to copy it in clipboard
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class TextBox extends GridBagDialog implements ActionListener,ClipboardOwner{
	private static final long serialVersionUID = 1L;
	final static int cx[]={0,0,1};
	final static int cy[]={0,1,1};
	final static int cw[]={2,1,1};
	final static int ch[]={1,1,1};
	final static int xw[]={8,1,1};
	final static int yw[]={8,0,0} ;
	final static int cf[]={B,H,H};
	final int width=480;
	final int height=320;
	private JTextArea dtext;
	private JButton copyButton,exitButton ;
	public TextBox(JFrame parent,String title,String text){
		super(parent,title,false,cx,cy,cw,ch,xw,yw,cf);
		setSize(width,height);
		container=getContentPane();
		container.setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		dtext=new JTextArea(text);
		addWithConstraints(0,new JScrollPane(dtext));
		copyButton = new JButton ("Copy whole to Clipboard");
		addWithConstraints(1,copyButton);		
		copyButton.addActionListener(this);
		exitButton = new JButton ("Exit");
		addWithConstraints(2,exitButton);
		exitButton.addActionListener(this);
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
	}
	public void actionPerformed (ActionEvent e){
		if (e.getSource()==copyButton) setClipboardContents(dtext.getText());
		if (e.getSource()==exitButton) dispose();
	}
	public void setClipboardContents(String aString){
	    StringSelection stringSelection = new StringSelection(aString);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(stringSelection, this);
	  }
	public void lostOwnership(Clipboard aClipboard, Transferable aContents) {}
}