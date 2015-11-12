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
 * Preselect source and target by updating preselected node attribute
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class PreselectSrcTgt extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="Preselect Sources and Targets";
	public PreselectSrcTgt(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap1);
		this.adapter = adapter;
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		ModelMenuUtils menuUtils = new ModelMenuUtils(applicationManager.getCurrentNetwork(),swingApplication.getJFrame());
		WeightGraphStructure wgs = new WeightGraphStructure(applicationManager.getCurrentNetwork());	
		menuUtils.getSrcTgt(wgs,title);
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
