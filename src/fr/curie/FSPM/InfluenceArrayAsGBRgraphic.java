package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2013 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
/**
 * Display Influence Array as Paved Window in green, black, red
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class InfluenceArrayAsGBRgraphic extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="Display Influence Array as Green Black Red Paved Window";
	public InfluenceArrayAsGBRgraphic(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());		
		setPreferredMenu(Ttls.app+Ttls.chap4);
		this.adapter = adapter;
	}
	public void actionPerformed(ActionEvent e) {
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		(new InfluenceArrayAsGraphic(title,applicationManager.getCurrentNetwork(),false,swingApplication.getJFrame())).pavingData();

	}
}

