package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
 */
import java.awt.event.ActionEvent;
import java.util.HashSet;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
/**
 * this class selects nodes and edges between 2 lists of nodes
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class FromToNodes extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	final public static String title="Select Sub-network from Sources to Targets";
	public FromToNodes(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}		
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());
		ModelMenuUtils menuUtils = new ModelMenuUtils(wgs,swingApplication.getJFrame());		
		menuUtils.getSrcTgt(title);
		for(CyNode node:applicationManager.getCurrentNetwork().getNodeList())
			applicationManager.getCurrentNetwork().getRow(node).set(CyNetwork.SELECTED,false);	
		HashSet<Integer> nodes=wgs.extractNodes(menuUtils.srcDialog,menuUtils.tgtDialog);		
		for(int node:nodes)
			applicationManager.getCurrentNetwork().getRow(wgs.nodes.get(node)).set(CyNetwork.SELECTED,true);
		applicationManager.getCurrentNetworkView().updateView();
	}
}
