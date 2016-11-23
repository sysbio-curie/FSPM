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
 * Preselect source and target by updating preselected node attribute 
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class PreselectSrcTgt extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final public static String title="Preselect Sources and Targets";
	public PreselectSrcTgt(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		WeightGraphStructure wgs = new WeightGraphStructure(applicationManager.getCurrentNetwork());
		ModelMenuUtils menuUtils = new ModelMenuUtils(wgs,swingApplication.getJFrame());
		menuUtils.getSrcTgt(title);
		if(applicationManager.getCurrentNetwork().getDefaultNodeTable().getColumn(menuUtils.preselectAttrib)==null)
			applicationManager.getCurrentNetwork().getDefaultNodeTable().createColumn(menuUtils.preselectAttrib, Integer.class, false);		
		for(int n=0;n<wgs.nodes.size();n++) 
			applicationManager.getCurrentNetwork().getRow(wgs.nodes.get(n)).set(menuUtils.preselectAttrib,menuUtils.notSelected);
		for(int n:menuUtils.srcDialog) 
			applicationManager.getCurrentNetwork().getRow(wgs.nodes.get(n)).set(menuUtils.preselectAttrib,menuUtils.selectedAsSrc);
		for(int n:menuUtils.tgtDialog){
			if(menuUtils.srcDialog.contains(n)) 
				applicationManager.getCurrentNetwork().getRow(wgs.nodes.get(n)).set(menuUtils.preselectAttrib,menuUtils.selectAsSrcTgt);
			else 
				applicationManager.getCurrentNetwork().getRow(wgs.nodes.get(n)).set(menuUtils.preselectAttrib,menuUtils.selectedAsTgt);
		}
	}
}
