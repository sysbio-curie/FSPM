package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
/** 
 * Common dialogs to input data
 * - reach, score threshold
 * - sources and targets for influence with preselected nodes
 * - only sources for reach area
 * Adapt title to context 
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class ModelMenuUtils {	
	WeightGraphStructure wgs;
	JFrame frame;
	protected ArrayList<Integer> srcDialog;
	protected ArrayList<Integer> tgtDialog;
	final String preselectAttrib="PRESELECTED";
	final int notSelected=0;
	final int selectedAsSrc=1;
	final int selectedAsTgt=2;
	final int selectAsSrcTgt=3;
	final String reachAttrib="INFLUENCE_REACH";
	final String thresholdAttrib="SCORE_THRESHOLD";	
	final public String errorWeigth="Influence weigth attribute not rigthly updated\r\nCannot display influence";
	public ModelMenuUtils(WeightGraphStructure wgs,JFrame frame){
		this.wgs=wgs;
		this.frame=frame;
	}
	String title(String title){
		return title+":"+wgs.getName()+"(reach="+wgs.reach+")";
	}
	String title(String title,double threshold){
		return title+":"+wgs.getName()+"(reach="+wgs.reach+",threshold="+threshold+")";
	}
	double getReach(){
		Double reach=wgs.net.getRow(wgs.net).get(reachAttrib,Double.class);
		if(reach==null) reach=inputReach();
		return reach;
	}
	double inputReach(){
		double reach;
		String input=JOptionPane.showInputDialog(frame,"Input the number of paths beyond the signal is under 5%","Parameter of signal fading",JOptionPane.QUESTION_MESSAGE);		
		try{
			if(wgs.net.getDefaultNetworkTable().getColumn(reachAttrib)==null) wgs.net.getDefaultNetworkTable().createColumn(reachAttrib,Double.class,false);			
			reach=Double.valueOf(input);
			wgs.net.getRow(wgs.net).set(reachAttrib,reach);
			return reach;
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(frame,"Wrong input for "+reachAttrib+", a real as 9.9\r\nIf not updated in network table, input directly in column","Warning",JOptionPane.ERROR_MESSAGE);
			return 0.0;
		}	
	}
	double getThreshold(){
		Double threshold=wgs.net.getRow(wgs.net).get(thresholdAttrib,Double.class);
		if(threshold==null) threshold=inputThreshold();
		return threshold;
	}
	double inputThreshold(){
		double threshold;
		String input=JOptionPane.showInputDialog(frame,"Input the score threshold (TS) eg 0.05 \r\nIf activity<-ST, as inhibited\r\nIf activity>+ST, as activated","Threshold for computing score",JOptionPane.QUESTION_MESSAGE);		
		try{
			if(wgs.net.getDefaultNetworkTable().getColumn(thresholdAttrib)==null) wgs.net.getDefaultNetworkTable().createColumn(thresholdAttrib,Double.class,false);			
			threshold=Double.valueOf(input);	
			wgs.net.getRow(wgs.net).set(thresholdAttrib, threshold);
			return threshold;
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(frame,"Wrong input for "+thresholdAttrib+", a real as 0.09"+"/r/nIf not updated in network table, input directly in column","Warning",JOptionPane.ERROR_MESSAGE);
			return Double.MAX_VALUE;
		}	
	}
	boolean isWeighted(String title){
		if(!wgs.initWeights()){
			JOptionPane.showMessageDialog(frame,errorWeigth,title,JOptionPane.ERROR_MESSAGE);
			return false;
		}else return true;	
	}
	public CyNode getNodeWithName(Object value){
		CyTable table=wgs.net.getDefaultNodeTable();
		Collection<CyRow> matchingRows=table.getMatchingRows(CyNetwork.NAME,value);
		String primaryKeyColname = table.getPrimaryKey().getName();
		for (CyRow row : matchingRows){
			Long nodeId=row.get(primaryKeyColname,Long.class);
			if (nodeId==null) continue;
			CyNode node=wgs.net.getNode(nodeId);
			if (node==null) continue;
			return node;
		}
		return null;
	}
	protected void getAllSrcAllTgt(){
		srcDialog=new ArrayList<Integer>();
		tgtDialog=new ArrayList<Integer>();
		for(int i=0;i<wgs.nodes.size();i++) srcDialog.add(i);
		for(int i=0;i<wgs.nodes.size();i++) tgtDialog.add(i);	
	}
	protected void getSrcAllTgt(String title){
		srcDialog=new ArrayList<Integer>();
		tgtDialog=new ArrayList<Integer>();
		for(int i=0;i<wgs.nodes.size();i++) tgtDialog.add(i);
		String[] nodeNames=new String[wgs.nodes.size()];
		for(int i=0;i<wgs.nodes.size();i++) nodeNames[i]=wgs.getName(wgs.nodes.get(i));
		ArrayList<String> selection=new ArrayList<String>();
		ListDialog listBox=new ListDialog(frame,title,"Select Start Nodes",nodeNames);
		listBox.launchDialog(selection);		
		for(String nodeId:selection) srcDialog.add(wgs.nodes.indexOf(getNodeWithName(nodeId)));
	}
	int[] copy(ArrayList<Integer> from){
		if(from.isEmpty()) return null;
		else{
			int[] to=new int[from.size()];
			for(int i=0;i<from.size();i++) to[i]=from.get(i); 
			return to;
		}
	}
	protected void getSrcTgt(String title){
		ArrayList<Integer> preSrc=new ArrayList<Integer>();
		ArrayList<Integer> preTgt=new ArrayList<Integer>();		
		for(int n=0;n<wgs.nodes.size();n++){
			Integer s=wgs.net.getRow(wgs.nodes.get(n)).get(preselectAttrib,Integer.class);
			if(s!=null) switch(s){
			case selectedAsSrc:preSrc.add(n);
			break;
			case selectedAsTgt:preTgt.add(n);
			break;
			case selectAsSrcTgt:preSrc.add(n);preTgt.add(n);
			}		
		}
		srcDialog=new ArrayList<Integer>();
		tgtDialog=new ArrayList<Integer>();
		String[] nodeNames=new String[wgs.nodes.size()];
		int n=0;for(CyNode node:wgs.nodes) nodeNames[n++]=wgs.getName(node);
		ArrayList<String> selectNode1=new ArrayList<String>();
		ArrayList<String> selectNode2=new ArrayList<String>();
		TwoListDialog dialog=new TwoListDialog(frame,title,"Select nodes as","Source","Target",nodeNames,nodeNames);
		dialog.launchDialog(copy(preSrc),copy(preTgt),selectNode1,selectNode2);
		for(String nodeId:selectNode1) 
			srcDialog.add(wgs.nodes.indexOf(getNodeWithName(nodeId)));
		for(String nodeId:selectNode2) 
			tgtDialog.add(wgs.nodes.indexOf(getNodeWithName(nodeId)));
	}	
}
