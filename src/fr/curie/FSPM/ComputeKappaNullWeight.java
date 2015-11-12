package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
/**
 * Overload methods from ComputeKappa to null a weight
 * 
 * @author Daniel.Rovera@Curie.fr
 *
 */
public class ComputeKappaNullWeight extends ComputeKappa {
	public ComputeKappaNullWeight(CyApplicationManager applicationManager,CySwingApplication swingApplication) {
		super(applicationManager, swingApplication);
	}
	public void changeWeight(int edge){
		wgs.weights.set(edge,0.0);
	}
	double weightValue(int edge){return 0.0;}
}
