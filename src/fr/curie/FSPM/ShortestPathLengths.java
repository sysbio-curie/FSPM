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
 * Display shortest path array computed by BFS
 * @author Daniel.Rovera@Curie.fr or @gmail.com
 */
public class ShortestPathLengths extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final public static String title="Shortest Path Length Array";
	public ShortestPathLengths(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();		
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());
		ModelMenuUtils menuUtils = new ModelMenuUtils(wgs,swingApplication.getJFrame());	
		menuUtils.getSrcTgt(title);
		if(menuUtils.srcDialog.isEmpty()|menuUtils.tgtDialog.isEmpty()) return;
		TextBox text=new TextBox(swingApplication.getJFrame(),title+" "+wgs.getName(),0.5,0.9);		
		(new WeightGraphTools(wgs,menuUtils.srcDialog,menuUtils.tgtDialog,text)).shortPathMatrix();
		text.setVisible(true);
	}
}
