package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2011 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE  
*/
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * A dialog box to select strings in two lists displaying selected strings
 * 2 arrayLists are filled of selected values
 * Used to select sources and targets
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class TwoListDialog extends GridBagDialog implements ActionListener,ListSelectionListener{
	private static final long serialVersionUID = 1L;
	public static int cx[]={0,0,1,0,1,0,1,0,1};
	public static int cy[]={0,1,1,2,2,3,3,4,4};
	public static int cw[]={2,1,1,1,1,1,1,1,1};
	public static int ch[]={1,1,1,1,1,1,1,1,1};
	public static int xw[]={0,0,0,9,9,4,4,1,1};
	public static int yw[]={0,0,0,9,9,4,4,0,0};
	public static int cf[]={H,H,H,B,B,B,B,H,H};
	final int width=360;
	final int height=480;
	private JButton okBouton, cancelBouton ;
	private boolean ok = false ;
	private JList<String> listBox1,listBox2;
	private List list1,list2;
	public TwoListDialog(JFrame parent,String title, String label,String label1,String label2,String[]values1,String[]values2){ 
		super(parent,title,true,cx,cy,cw,ch,xw,yw,cf);
		setSize(width,height);
		container=getContentPane();
		container.setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		addWithConstraints(0,new JLabel(label,JLabel.CENTER));
		addWithConstraints(1,new JLabel(label1,JLabel.CENTER));
		addWithConstraints(2,new JLabel(label2,JLabel.CENTER));
		listBox1 = new JList<String>(values1);
		addWithConstraints(3,new JScrollPane(listBox1));
		listBox1.addListSelectionListener(this);
		listBox2 = new JList<String>(values2);
		addWithConstraints(4,new JScrollPane(listBox2));
		listBox2.addListSelectionListener(this);
		list1=new List();
		addWithConstraints(5,list1);
		list1.setFont(listBox1.getFont());
		list2=new List();
		addWithConstraints(6,list2);
		list2.setFont(listBox2.getFont());
		okBouton = new JButton ("OK") ;
		addWithConstraints(7,okBouton);		
		okBouton.addActionListener(this) ;
		cancelBouton = new JButton ("Cancel") ;
		addWithConstraints(8,cancelBouton) ;
		cancelBouton.addActionListener(this);
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
	}
	public void launchDialog(int[] startSelection1,int[] startSelection2,ArrayList<String> values1,ArrayList<String> values2){
		ok=false ;
		if(startSelection1!=null) listBox1.setSelectedIndices(startSelection1);
		if(startSelection2!=null) listBox2.setSelectedIndices(startSelection2);
		setVisible(true) ;
		values1.clear();
		values2.clear();
		if(ok){
			values1.addAll(listBox1.getSelectedValuesList());
			values2.addAll(listBox2.getSelectedValuesList());
		}
	}
	public void actionPerformed (ActionEvent e){
		if (e.getSource()==okBouton){
			ok=true;
			dispose();
		}
		if (e.getSource()==cancelBouton) dispose();
	}
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()){
			list1.removeAll();
			for(String s:listBox1.getSelectedValuesList()) list1.add(s);
			list2.removeAll();
			for(String s:listBox2.getSelectedValuesList()) list2.add(s);
		}		
	}
}

