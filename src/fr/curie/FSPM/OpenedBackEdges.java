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
import org.cytoscape.model.CyNetwork;
/**
 * List opened back edges by sources during DFS
 * MultiPath model only
 * So avoid loops in adjacency matrix and allow using actThroughTree
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class OpenedBackEdges extends AbstractCyAction {	
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="List Opened Edges MultiPath Only";
	public OpenedBackEdges(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap1);
		this.adapter = adapter;
	}
	public void actionPerformed(ActionEvent e){		
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		ModelMenuUtils menuUtils = new ModelMenuUtils(applicationManager.getCurrentNetwork(),swingApplication.getJFrame());
		ComputingByDFS dfs=new ComputingByDFS(applicationManager.getCurrentNetwork(),menuUtils.maxDepth());		
		menuUtils.getSrcTgt(dfs,title);
		StringBuffer txt=new StringBuffer("Back Edges by Sources Opened for No Loop, MultiPath only, max depth="+menuUtils.maxDepth()+"\r\n");
		for(int s:menuUtils.srcDialog){
			txt.append(applicationManager.getCurrentNetwork().getRow(dfs.nodes.get(s)).get(CyNetwork.NAME, String.class));			
			for(int edge:dfs.backEdgesByDFS(s,menuUtils.maxDepth())){
				txt.append("\t");
				txt.append(applicationManager.getCurrentNetwork().getRow(dfs.edges.get(edge)).get(CyNetwork.NAME, String.class));			
			}				
			txt.append("\r\n");
		}
		new TextBox(swingApplication.getJFrame(),title+"/MultiPath",txt.toString()).setVisible(true);
	}
}
