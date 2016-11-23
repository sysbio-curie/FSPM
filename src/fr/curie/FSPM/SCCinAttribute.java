package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
/**
 * Match nodes and strong connected components by attribute in table
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class SCCinAttribute  extends AbstractCyAction {	
	private static final long serialVersionUID = 1L;
	final static String title="SCC in Attribute";
	final String nodeColumnName="SCC";
	public SCCinAttribute(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e) {		
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CyNetwork network=applicationManager.getCurrentNetwork();
		SCCinTable graph=new SCCinTable(network);
		ArrayList<HashSet<Integer>> scc=graph.SCC();
		if(network.getDefaultNodeTable().getColumn(nodeColumnName)==null) 
			network.getDefaultNodeTable().createColumn(nodeColumnName,String.class,false);
		int sccNb=0;
		for(int i=0;i<scc.size();i++){
			if(scc.get(i).size()>1){
				sccNb++;
				for(int n:scc.get(i)) network.getRow(graph.nodes.get(n)).set(nodeColumnName,"SCC"+sccNb);
			}else{
				network.getRow(graph.nodes.get(scc.get(i).iterator().next())).set(nodeColumnName,"NotInSCC");
			}
		}	
	}
}