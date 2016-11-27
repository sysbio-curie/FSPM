package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE  
*/
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Text window with pop-up menu to copy and close
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class TextBox extends JDialog implements ClipboardOwner{
	private static final long serialVersionUID = 1L;
	private JTextArea jtext;
	private JPopupMenu popup;
	final String[] items={"Copy Whole","Copy Selected","Close"};
	public TextBox(JFrame parent,String title,double wScreen,double hScreen){
		super(parent);
		init(title,wScreen,hScreen);
	}
	public TextBox(JFrame parent,String title,String text,double wScreen,double hScreen){
		super();
		init(title,wScreen,hScreen);
		jtext.setText(text);
	}
	private void init(String title,double wScreen,double hScreen){
		setTitle(title);
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int)(scr.width*wScreen),(int)(scr.height*hScreen));
		jtext=new JTextArea();
		add(new JScrollPane(jtext),BorderLayout.CENTER);
		popup = new JPopupMenu();
		ActionListener menuListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int item=0;
				while(!items[item].equals(event.getActionCommand())) item++;
				switch(item){
				case 0:setClipboardContents(jtext.getText());break;
				case 1:setClipboardContents(jtext.getSelectedText());break;
				case 2:dispose();break;
				}				
			}
		};
	    JMenuItem item;
	    for(int i=0;i<items.length;i++){
	    	popup.add(item=new JMenuItem(items[i]));
	    	item.addActionListener(menuListener);
	    }
	    jtext.addMouseListener(new MousePopupListener());
	    addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
	}
	class MousePopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e){checkPopup(e);}
		public void mouseClicked(MouseEvent e){checkPopup(e);}
		public void mouseReleased(MouseEvent e){checkPopup(e);}
		private void checkPopup(MouseEvent e) {if (e.isPopupTrigger()) popup.show(TextBox.this, e.getX(), e.getY());}
	}
	public void set(String text){
		jtext.setText(text);
	}
	public void append(String text){		
		jtext.setCaretPosition(jtext.getDocument().getLength());
		jtext.append(text);		
	}
	private void setClipboardContents(String aString){
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}
	public void lostOwnership(Clipboard aClipboard, Transferable aContents){}
}