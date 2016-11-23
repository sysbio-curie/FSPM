package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
/**
 * Delete one node and reconnect edges
 * Compute the new weights by multiplying
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class DeleteNodeAndReconnect extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	final public static String title="Delete One Node and Reconnect Weighted Edges";
	public DeleteNodeAndReconnect(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		CyNetwork network=applicationManager.getCurrentNetwork();
		WeightGraphStructure wgs=new WeightGraphStructure(network);
		if(!wgs.initWeights()){
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),(new ModelMenuUtils(wgs,swingApplication.getJFrame())).errorWeigth,title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		List<CyNode> selected=CyTableUtil.getNodesInState(network,"selected",true);
		if(selected.size()!=1){
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),"Select Only One Node",title,JOptionPane.ERROR_MESSAGE);
			return;
		}		
		int nodeToDel=wgs.nodes.indexOf(selected.get(0));
		ArrayList<Integer> upEdgeToDel=new ArrayList<Integer>();
		ArrayList<Integer> downEdgeToDel=new ArrayList<Integer>();
		for(int edge=0;edge<wgs.edges.size();edge++){
			if(wgs.tgts.get(edge)==nodeToDel) upEdgeToDel.add(edge);
			if(wgs.srcs.get(edge)==nodeToDel) downEdgeToDel.add(edge);			
		}
		int ue=upEdgeToDel.size();
		int de=downEdgeToDel.size();
		if(JOptionPane.showConfirmDialog(swingApplication.getJFrame(),"Confirm deleting "+ue+" To and "+ de+" From Edges\r\n"+"And Creating "+(ue*de)+ " Edges",title,JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION) return;
		network.removeNodes(Collections.singletonList(selected.get(0)));
		for(int u=0;u<ue;u++)for(int d=0;d<de;d++){
			CyNode src=wgs.nodes.get(wgs.srcs.get(upEdgeToDel.get(u)));
			CyNode tgt=wgs.nodes.get(wgs.tgts.get(downEdgeToDel.get(d)));
			CyEdge newEdge=network.addEdge(src,tgt,true);
			double weight=wgs.weights.get(upEdgeToDel.get(u))*wgs.weights.get(downEdgeToDel.get(d));
			String interaction;
			if(weight>0) interaction="ACTIVATION"; else if(weight<0) interaction="INHIBITION"; else interaction="PP";
			String newEdgeName=network.getRow(src).get(CyNetwork.NAME,String.class)+"("+interaction+")"+network.getRow(tgt).get(CyNetwork.NAME,String.class);
			network.getRow(newEdge).set(CyNetwork.NAME,newEdgeName);
			network.getRow(newEdge).set(CyEdge.INTERACTION,interaction);
			network.getRow(newEdge).set("WEIGHT",weight);		
		}
		applicationManager.getCurrentNetworkView().updateView();
	}
}
