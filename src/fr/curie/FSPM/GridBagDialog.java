package fr.curie.FSPM;
/*
   Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
   Copyright (C) 2010-2011 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE  
*/
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import javax.swing.JDialog;
/** 
 * Parent of dialog classes using parameters of GridBagDialog
 * 
 * @author Daniel.Rovera@curie.fr
 */
abstract public class GridBagDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	public final static int B=GridBagConstraints.BOTH;
	public final static int V=GridBagConstraints.VERTICAL;
	public final static int H=GridBagConstraints.HORIZONTAL;
	public final static int N=GridBagConstraints.NONE;
	int[] gridx,gridy,gridwidth,gridheight,weightx,weighty,fill;
	Container container;
	GridBagConstraints constraints;
	GridBagDialog(Frame owner, String title, boolean modal,int[]cx,int[]cy,int[]cw,int[]ch,int[]xw,int[]yw,int[]cf){
		super(owner,title,modal);
		gridx=cx;
		gridy=cy;
		gridwidth=cw;
		gridheight=ch;
		weightx=xw;
		weighty=yw;
		fill=cf;
	}
	public void addWithConstraints(int constraintNb,Component component){
		constraints.gridx=gridx[constraintNb];
		constraints.gridy=gridy[constraintNb] ;
		constraints.gridwidth=gridwidth[constraintNb];
		constraints.gridheight=gridheight[constraintNb] ;
		constraints.weightx=weightx[constraintNb];
		constraints.weighty = weighty[constraintNb];
		constraints.fill=fill[constraintNb];
		container.add(component,constraints);
	}
}
