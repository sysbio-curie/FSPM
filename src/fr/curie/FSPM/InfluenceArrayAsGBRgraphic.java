package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
/**
 * Display Influence Array as Paved Window in green, black, red
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class InfluenceArrayAsGBRgraphic extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	final public static String title="Influence Array as Green Black Red Paved Window";
	public InfluenceArrayAsGBRgraphic(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e) {
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		(new InfluenceArrayAsGraphic(title,applicationManager.getCurrentNetwork(),false,swingApplication.getJFrame())).pavingData();
	}
}

