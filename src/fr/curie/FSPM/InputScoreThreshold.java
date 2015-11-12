package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
/**
 * Action to input Score Threshold
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class InputScoreThreshold extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="Input Score Threshold";
	public InputScoreThreshold(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap2);
		this.adapter = adapter;
	}	
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		(new ModelMenuUtils(applicationManager.getCurrentNetwork(),swingApplication.getJFrame())).inputThreshold();
	}
}