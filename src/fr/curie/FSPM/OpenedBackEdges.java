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
 * List opened back edges by sources during DFS
 * So avoid loops in adjacency matrix and allow using ActThroughTree
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class OpenedBackEdges extends AbstractCyAction {	
	private static final long serialVersionUID = 1L;
	final public static String title="List Opened Edges";
	public OpenedBackEdges(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e){		
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());
		ModelMenuUtils menuUtils = new ModelMenuUtils(wgs,swingApplication.getJFrame());		
		wgs.reach=menuUtils.getReach();
		if(wgs.reach==0.0) return;	
		menuUtils.getSrcTgt(title);
		if(menuUtils.srcDialog.isEmpty()|menuUtils.tgtDialog.isEmpty()) return;
		wgs.adjustAdjacency(menuUtils.srcDialog,menuUtils.tgtDialog);
		WeightGraphTools wgt=new WeightGraphTools(wgs,menuUtils.srcDialog,menuUtils.tgtDialog,null);				
		StringBuffer txt=new StringBuffer("Back Edges by Sources Opened for No Loop, max depth="+wgs.getMaxDepth()+"\r\n");
		for(int s:menuUtils.srcDialog){
			txt.append(wgt.getName(wgt.nodes.get(s)));			
			for(int edge:wgt.backEdgesByDFS(s)){
				txt.append("\t");
				txt.append(wgt.getName(wgt.edges.get(edge)));			
			}				
			txt.append("\r\n");
		}
		new TextBox(swingApplication.getJFrame(),menuUtils.title(title),txt.toString(),0.5,0.9).setVisible(true);
	}
}
