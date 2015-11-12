package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2013 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import java.util.HashSet;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
/**
 * In menu, this class selects nodes and edges between 2 lists of nodes
 * by intersection of 2 sets made get by descending and ascending the graph
 * This class is also used as up class for other menu classes providing 
 * input of reach parameter,formatted output of arrays and two lists node dialog
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class FromToNodes extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="Select Sub-network from Sources to Targets";
	public FromToNodes(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap1);
		this.adapter = adapter;
	}		
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		ModelMenuUtils menuUtils = new ModelMenuUtils(applicationManager.getCurrentNetwork(),swingApplication.getJFrame());
		ComputingByBFS cpt=new ComputingByBFS(applicationManager.getCurrentNetwork());
		menuUtils.getSrcTgt(cpt,title);
		for(CyNode node:applicationManager.getCurrentNetwork().getNodeList())
			applicationManager.getCurrentNetwork().getRow(node).set(CyNetwork.SELECTED,false);	
		HashSet<Integer> nodes=cpt.extractNodes(menuUtils.srcDialog,menuUtils.tgtDialog);		
		for(int node:nodes)
			applicationManager.getCurrentNetwork().getRow(cpt.nodes.get(node)).set(CyNetwork.SELECTED,true);
		applicationManager.getCurrentNetworkView().updateView();
	}
}
