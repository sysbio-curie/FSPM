package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import javax.swing.JFrame;
import org.cytoscape.model.CyNetwork;
/**
 * Overload methods from ComputeKappa to null a weight 
 * @author Daniel.Rovera@Curie.fr or @gmail.com
 *
 */
public class ComputeKappaNullWeight extends ComputeKappa {
	public ComputeKappaNullWeight(CyNetwork network,JFrame frame) {
		super(network,frame);
	}
	private double weight;
	public void change(int edge){
		weight=wgs.weights.get(edge);
		wgs.weights.set(edge,0.0);		
	}
	public void restore(int edge){
		wgs.weights.set(edge,weight);		
	}
	String weightValue(int edge,boolean inTask){return "0.0";}
}
