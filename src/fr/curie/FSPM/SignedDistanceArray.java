package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
/**
 * Display matrix of signed distance between nodes
 * Distance is counted by number of edges and signed by signs of paths based on weights
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class SignedDistanceArray extends ActionTreeTask{
	private static final long serialVersionUID = 1L;
	final public static String title="Signed Distance Array";
	public SignedDistanceArray(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(FSPM_App_v2.app+section);
		insertSeparatorAfter();
	}
	public void actionPerformed(ActionEvent e){
		perform(title,ComputingThroughTree.SignedDistance.class);
	}
}
