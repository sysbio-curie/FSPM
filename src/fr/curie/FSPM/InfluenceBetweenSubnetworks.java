package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
/**
 * Display Influence Array between subnetworks
 * Compute influence array by adding influence from reference network
 * by group of nodes in sub-networks with the rule: same names imply same nodes
 * Build an array of arrays as subnetwork index, node index in WeightGraphStructure of reference network
 * Array targets in column and sources in row
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class InfluenceBetweenSubnetworks extends AbstractCyAction {	
	private static final long serialVersionUID = 1L;
	final public static String title="Influence Array Between Sub-Networks";
	CyApplicationManager applicationManager;
	CySwingApplication swingApplication;
	CyNetworkManager networkManager;
	StringBuffer warningForAbsentNode;
	final String messForAbsentNodes="There are nodes from sub-networks absent in reference network\r\n";
	public InfluenceBetweenSubnetworks(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}	
	ArrayList<ArrayList<Integer>> getNodesBySubnet(ArrayList<CyNetwork> subnets,WeightGraphStructure refWgs){
		Collections.sort(subnets,new Comparator<CyNetwork>(){
			public int compare(CyNetwork n1, CyNetwork n2){
				return n1.getRow(n1).get(CyNetwork.NAME,String.class).compareTo(n2.getRow(n2).get(CyNetwork.NAME,String.class));
		}});
		TreeMap<String,Integer> nameToIndex=new TreeMap<String,Integer>();
		for(int i=0;i<refWgs.nodes.size();i++) nameToIndex.put(refWgs.net.getRow(refWgs.nodes.get(i)).get(CyNetwork.NAME,String.class),i);
		ArrayList<ArrayList<Integer>> nodesBySubnet=new ArrayList<ArrayList<Integer>>(subnets.size());
		for(int i=0;i<subnets.size();i++) nodesBySubnet.add(new ArrayList<Integer>());
		for(int sn=0;sn<subnets.size();sn++){
			for(CyNode node:subnets.get(sn).getNodeList()){
				String name=subnets.get(sn).getRow(node).get(CyNetwork.NAME,String.class);
				Integer index=nameToIndex.get(name);
				if(index==null){
					warningForAbsentNode.append(name);
					warningForAbsentNode.append("\r\n");
				}else nodesBySubnet.get(sn).add(index);				
			}						
		}
		return nodesBySubnet;
	}
	public void actionPerformed(ActionEvent e){
		applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		networkManager=FSPM_App_v2.getAdapter().getCyNetworkManager();
		ArrayList<CyNetwork> subnets=new ArrayList<CyNetwork>(applicationManager.getSelectedNetworks());
		if(subnets.size()<2){
			if(JOptionPane.showConfirmDialog(swingApplication.getJFrame(),				
					"Less than 2 networks are selected, Confirm to Continue or Cancel\r\n (Selection of Several Networks in Network Panel by Control Click)",
					"List of Selected networks",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.CANCEL_OPTION) return;
		}
		TreeMap<String,CyNetwork> nameToNet=new TreeMap<String,CyNetwork>();
		for(CyNetwork net:networkManager.getNetworkSet()) nameToNet.put(net.getRow(net).get(CyNetwork.NAME,String.class),net);
		String[] netNames=new String[nameToNet.keySet().size()];
		int ni=0;for(String s:nameToNet.keySet()) netNames[ni++]=s;
		String selected=(String)JOptionPane.showInputDialog(swingApplication.getJFrame(),"Select a network as reference",title,JOptionPane.PLAIN_MESSAGE,null,netNames,netNames[0]);
		if(selected==null) return;
		CyNetwork refNet=nameToNet.get(selected);
		WeightGraphStructure refWgs=new WeightGraphStructure(refNet);
		ModelMenuUtils menuUtils = new ModelMenuUtils(refWgs,swingApplication.getJFrame());
		if(!refWgs.initWeights()){
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),menuUtils.errorWeigth,title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		warningForAbsentNode=new StringBuffer(messForAbsentNodes);
		ArrayList<ArrayList<Integer>> nodesBySubnet=getNodesBySubnet(subnets,refWgs);
		if(nodesBySubnet==null) return;
		refWgs.reach=menuUtils.getReach();
		if(refWgs.reach==0.0) return;		
		menuUtils.getAllSrcAllTgt();
		ComputingByTreeTask cDFS=new ComputingByTreeTask(new WeightGraphTools(refWgs,menuUtils.srcDialog,menuUtils.tgtDialog,null));
		Double[][] refInflMx=cDFS.allInfluence();
		Double[][] subnetInflMx=new Double[subnets.size()][subnets.size()];
		for(int t=0;t<subnets.size();t++) for(int s=0;s<subnets.size();s++) subnetInflMx[t][s]=0.0;
		for(int t=0;t<subnets.size();t++) for(int s=0;s<subnets.size();s++){
			for(int tt:nodesBySubnet.get(t))for(int ss:nodesBySubnet.get(s)){
				if(!refInflMx[tt][ss].isNaN()) subnetInflMx[t][s]=subnetInflMx[t][s]+refInflMx[tt][ss];
			}
		}
		StringBuffer txt=new StringBuffer();
		for(int t=0;t<subnets.size();t++) {
			txt.append("\t");
			txt.append(subnets.get(t).getRow(subnets.get(t)).get(CyNetwork.NAME,String.class));
		}
		txt.append("\r\n");
		for(int s=0;s<subnets.size();s++){
			txt.append(subnets.get(s).getRow(subnets.get(s)).get(CyNetwork.NAME, String.class));
			for(int t=0;t<subnets.size();t++){
				txt.append("\t");				
				txt.append(subnetInflMx[t][s]);				
			}
			txt.append("\r\n");
		}
		if(warningForAbsentNode.length()>messForAbsentNodes.length()) txt.append(warningForAbsentNode);
		new TextBox(swingApplication.getJFrame(),menuUtils.title(title),txt.toString(),0.5,0.9).setVisible(true);
	}
}