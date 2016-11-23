package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.util.ArrayList;

import javax.swing.JFrame;

import org.cytoscape.model.CyNetwork;
/**
 * Overload methods from ComputeKappa to open edge using adjacency
 * @author Daniel.Rovera@Curie.fr or @gmail.com
 *
 */
public class ComputeKappaOpenEdge  extends ComputeKappa {
	ArrayList<Integer> keptList;
	public ComputeKappaOpenEdge(CyNetwork network,JFrame frame) {
		super(network,frame);
	}
	public void change(int edge){
		keptList=wgs.adjacency.get(wgs.srcs.get(edge));
		ArrayList<Integer> withoutEdge=new ArrayList<Integer>(keptList);
		withoutEdge.remove((Integer)edge);
		wgs.adjacency.set(wgs.srcs.get(edge),withoutEdge);
	}
	public void restore(int edge){
		wgs.adjacency.set(wgs.srcs.get(edge),keptList);
	}
	String weightValue(int edge,boolean inTask){return "open";}
}
