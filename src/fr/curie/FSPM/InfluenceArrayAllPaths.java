package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
/**
 * Display influence as an array computed through all paths
 * for computing, non connected=0, all possible digits
 * @author Daniel.Rovera@Curie.fr or @gmail.com
 */
public class InfluenceArrayAllPaths extends ActionPathTask{
	private static final long serialVersionUID = 1L;
	final public static String title="Influence Array All Paths";
	public InfluenceArrayAllPaths(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e){
		perform(title,ComputingThroughPath.InfluenceArrayAll.class,true);
	}
}