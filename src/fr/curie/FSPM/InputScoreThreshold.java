package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
/**
 * Action to input Score Threshold
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class InputScoreThreshold extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	final public static String title="Input Score Threshold";
	public InputScoreThreshold(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}	
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		(new ModelMenuUtils(new WeightGraphStructure(applicationManager.getCurrentNetwork()),swingApplication.getJFrame())).inputThreshold();
	}
}