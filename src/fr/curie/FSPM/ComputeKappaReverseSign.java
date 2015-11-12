package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
/**
 * Overload methods from ComputeKappa to reverse sign weight
 * 
 * @author Daniel.Rovera@Curie.fr
 *
 */
public class ComputeKappaReverseSign extends ComputeKappa {
	public ComputeKappaReverseSign(CyApplicationManager applicationManager,CySwingApplication swingApplication) {
		super(applicationManager, swingApplication);
	}
	public void changeWeight(int edge){
		wgs.weights.set(edge,-wgs.weights.get(edge));
	}
	double weightValue(int edge){return -wgs.weights.get(edge);}
}

