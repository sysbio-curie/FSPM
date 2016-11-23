package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import javax.swing.JFrame;
import org.cytoscape.model.CyNetwork;
/**
 * Overload methods from ComputeKappa to reverse sign weight
 * @author Daniel.Rovera@Curie.fr or @gmail.com
 *
 */
public class ComputeKappaReverseSign extends ComputeKappa {
	public ComputeKappaReverseSign(CyNetwork network,JFrame frame) {
		super(network,frame);
	}
	public void change(int edge){
		wgs.weights.set(edge,-wgs.weights.get(edge));
	}
	public void restore(int edge){
		wgs.weights.set(edge,-wgs.weights.get(edge));
	}
	String weightValue(int edge,boolean inTask){if(inTask) return Double.toString(wgs.weights.get(edge)); else return Double.toString(-wgs.weights.get(edge));}
}

