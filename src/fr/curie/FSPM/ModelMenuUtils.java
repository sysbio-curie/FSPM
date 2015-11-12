package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2013 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
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
 * - reach and compute fade
 * - score threshold
 * - model type
 * - sources and targets for influence with preselected nodes
 * - only sources for reach area
 * Common display functions for arrays
 *  
 * @author Daniel.Rovera@curie.fr
 */
public class ModelMenuUtils {	
	private static final long serialVersionUID = 1L;
	CyNetwork net;
	JFrame frame;
	protected ArrayList<Integer> srcDialog;
	protected ArrayList<Integer> tgtDialog;
	final String preselectAttrib="PRESELECTED";
	final int notSelected=0;
	final int selectedAsSrc=1;
	final int selectedAsTgt=2;
	final int selectAsSrcTgt=3;
	boolean ifMultiPath=false;
	final String pathModelAttrib="PATH_MODEL";
	Double reach;
	final double reachDefault=5.0;
	final String reachAttrib="INFLUENCE_REACH";
	double fade;
	Double threshold;
	final double thresholdDefault=0.05;
	final String thresholdAttrib="SCORE_THRESHOLD";	
	final public String errorWeigth="Influence weigth attribute not rigthly updated\r\nCannot display influence";
	ModelMenuUtils(CyNetwork net,JFrame frame){
		this.net=net;
		this.frame=frame;
	}
	protected String addTitle(String title){
		title=title+"/"+reach+"/";
		if(ifMultiPath) title=title+"MultiPath"; else title=title+"MonoPath";
		return title;
	}
	private boolean checkColumn(String columnName, Class<?> type){
		try{
			if(net.getDefaultNetworkTable().getColumn(columnName)==null)
				net.getDefaultNetworkTable().createColumn(columnName,type,false);
			else{
				net.getDefaultNetworkTable().deleteColumn(columnName);
				net.getDefaultNetworkTable().createColumn(columnName,type,false);
			}
			return true;
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(frame,"Application cannot delete columns named "+columnName+"\r\nDelete them by hands and start again input","Error by Application",JOptionPane.ERROR_MESSAGE);
			net.getRow(net).set(reachAttrib, reachDefault);
			return false;
		}
	}
	protected void updatePathModel(){
		String model=net.getRow(net).get(pathModelAttrib,String.class);
		if(model==null) inputPathModel(); else{
			if(model.equals("MONO")) ifMultiPath=false; 
			if(model.equals("MULTI")) ifMultiPath=true;
		}
	}
	protected void inputPathModel(){
		AlternativeDialog dialog=new AlternativeDialog(frame,ChooseModelType.title,"Click on choosen path exploring mode","Mono Path Mode","Multi Path Mode");		
		if(!checkColumn(pathModelAttrib,String.class)) return;		
		int option=dialog.getOption();
		if(option==1){
			net.getRow(net).set(pathModelAttrib, "MONO");
			ifMultiPath=false;			
			return;
		}
		if(option==2){
			net.getRow(net).set(pathModelAttrib, "MULTI");
			ifMultiPath=true;			
			return;
		}
		net.getRow(net).set(pathModelAttrib,"MONO");
		ifMultiPath=false;		
		JOptionPane.showMessageDialog(frame,"Default value: MONO","Warning",JOptionPane.ERROR_MESSAGE);
		return;
	}
	protected void updateFade(){;
		reach=net.getRow(net).get(reachAttrib,Double.class);
		if(reach==null) inputReach();	
		fade=Math.exp(Math.log(0.05)/reach);
	}	
	protected void inputReach(){
		String input=JOptionPane.showInputDialog(frame,"Input the number of paths beyond the signal is under 5%","Parameter of signal fading",JOptionPane.QUESTION_MESSAGE);		
		if(!checkColumn(reachAttrib,Double.class)) return;		
		try{
			reach=Double.valueOf(input);
			net.getRow(net).set(reachAttrib, reach);
		}
		catch(Exception e){
			reach=reachDefault;
			JOptionPane.showMessageDialog(frame,"Wrong input, default value "+reach,"Warning",JOptionPane.ERROR_MESSAGE);
			net.getRow(net).set(reachAttrib, reachDefault);
		}	
	}
	protected int maxDepth(){
		reach=net.getRow(net).get(reachAttrib, Double.class);
	    if (reach==null) inputReach();
		return (reach.intValue()*2+1);
	}
	protected void updateThreshold(){
		threshold=net.getRow(net).get(thresholdAttrib, Double.class);
		if(threshold==null) inputThreshold();	
	}
	protected void inputThreshold(){
		String input=JOptionPane.showInputDialog(frame,"Input the score threshold TS\r\nIf activity<-TS, as inhibited\r\nIf activity>+TS, as activated","Threshold for computing score",JOptionPane.QUESTION_MESSAGE);		
		if (!checkColumn(thresholdAttrib,Double.class)) return;		
		try{
			threshold=Math.abs(Double.valueOf(input));				
			net.getRow(net).set(thresholdAttrib, threshold);
		}
		catch(Exception e){
			threshold=thresholdDefault;
			JOptionPane.showMessageDialog(frame,"Wrong input, default value "+threshold,"Warning",JOptionPane.ERROR_MESSAGE);
			net.getRow(net).set(thresholdAttrib, thresholdDefault);
		}	
	}
	public static CyNode getNodeWithName(CyNetwork net,CyTable table,String colname,Object value){
		final Collection<CyRow> matchingRows=table.getMatchingRows(colname,value);
		final String primaryKeyColname = table.getPrimaryKey().getName();
		for (final CyRow row : matchingRows){
			final Long nodeId=row.get(primaryKeyColname,Long.class);
			if (nodeId==null) continue;
			final CyNode node=net.getNode(nodeId);
			if (node==null) continue;
			return node;
		}
		return null;
	}
	protected void getAllSrcAllTgt(WeightGraphStructure wgs){
		srcDialog=new ArrayList<Integer>();
		tgtDialog=new ArrayList<Integer>();
		for(int i=0;i<wgs.nodes.size();i++) srcDialog.add(i);
		for(int i=0;i<wgs.nodes.size();i++) tgtDialog.add(i);	
	}
	protected void getSrcAllTgt(WeightGraphStructure wgs,String title, CyNetwork net){
		srcDialog=new ArrayList<Integer>();
		tgtDialog=new ArrayList<Integer>();
		for(int i=0;i<wgs.nodes.size();i++) tgtDialog.add(i);
		String[] nodeNames=new String[wgs.nodes.size()];
		for(int i=0;i<wgs.nodes.size();i++) nodeNames[i]= net.getRow( wgs.nodes.get(i)).get(CyNetwork.NAME, String.class);	
		ArrayList<String> selection=new ArrayList<String>();
		ListDialog listBox=new ListDialog(frame,title,"Select Start Nodes",nodeNames);
		listBox.launchDialog(selection);		
		for(String nodeId:selection) srcDialog.add(wgs.nodes.indexOf(getNodeWithName(net,net.getDefaultNodeTable(),"name",nodeId)));
	}
	int[] copy(ArrayList<Integer> from){
		if(from.isEmpty()) return null;
		else{
			int[] to=new int[from.size()];
			for(int i=0;i<from.size();i++) to[i]=from.get(i); 
			return to;
		}
	}
	protected void getSrcTgt(WeightGraphStructure wgs,String title){
		ArrayList<Integer> preSrc=new ArrayList<Integer>();
		ArrayList<Integer> preTgt=new ArrayList<Integer>();		
		for(int n=0;n<wgs.nodes.size();n++){
			Integer s=net.getRow(wgs.nodes.get(n)).get(preselectAttrib,Integer.class);
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
		int n=0;for(CyNode node:wgs.nodes) nodeNames[n++]=net.getRow(node).get(CyNetwork.NAME, String.class);
		ArrayList<String> selectNode1=new ArrayList<String>();
		ArrayList<String> selectNode2=new ArrayList<String>();
		TwoListDialog dialog=new TwoListDialog(frame,title,"Select nodes as","Source","Target",nodeNames,nodeNames);
		dialog.launchDialog(copy(preSrc),copy(preTgt),selectNode1,selectNode2);
		for(String nodeId:selectNode1) 
			srcDialog.add(wgs.nodes.indexOf(getNodeWithName(net,net.getDefaultNodeTable(),"name",nodeId)));
		for(String nodeId:selectNode2) 
			tgtDialog.add(wgs.nodes.indexOf(getNodeWithName(net,net.getDefaultNodeTable(),"name",nodeId)));
	}	
	String matrixToTxt(WeightGraphStructure wgs,Object[][] matrix, CyNetwork net){
		StringBuffer txt=new StringBuffer();
		for(int s=0;s<srcDialog.size();s++) {
			txt.append("\t");
			txt.append(net.getRow(wgs.nodes.get(srcDialog.get(s))).get(CyNetwork.NAME, String.class));
		}
		txt.append("\r\n");
		for(int t=0;t<tgtDialog.size();t++){
			txt.append(net.getRow(wgs.nodes.get(tgtDialog.get(t))).get(CyNetwork.NAME, String.class));
			for(int s=0;s<srcDialog.size();s++){
				txt.append("\t");
				txt.append(matrix[tgtDialog.get(t)][srcDialog.get(s)]);
			}
			txt.append("\r\n");
		}
		return txt.toString();
	}
	String matrixToList(WeightGraphStructure wgs,Double[][] matrix, CyNetwork net){
		StringBuffer txt=new StringBuffer("Source\tTarget\tInfluence\r\n");;
		for(int s=0;s<srcDialog.size();s++)for(int t=0;t<tgtDialog.size();t++){			
			txt.append(net.getRow(wgs.nodes.get(srcDialog.get(s))).get(CyNetwork.NAME, String.class));
			txt.append("\t");
			txt.append(net.getRow(wgs.nodes.get(tgtDialog.get(t))).get(CyNetwork.NAME, String.class));
			txt.append("\t");
			if(matrix[tgtDialog.get(t)][srcDialog.get(s)].isNaN()) txt.append(0.0);
			else txt.append(matrix[tgtDialog.get(t)][srcDialog.get(s)]);
			txt.append("\r\n");
		}
		return txt.toString();
	}
	String matrixToFormatTxt(WeightGraphStructure wgs,Double[][] matrix,String decFormatLibel, CyNetwork net){
		DecimalFormat decFormat;
		if(decFormatLibel==null) decFormat=null; else decFormat=new DecimalFormat(decFormatLibel);
		StringBuffer txt=new StringBuffer();
		for(int s=0;s<srcDialog.size();s++) {
			txt.append("\t");
			txt.append(net.getRow(wgs.nodes.get(srcDialog.get(s))).get(CyNetwork.NAME, String.class));
		}
		txt.append("\r\n");
		for(int t=0;t<tgtDialog.size();t++){
			txt.append(net.getRow(wgs.nodes.get(tgtDialog.get(t))).get(CyNetwork.NAME, String.class));
			for(int s=0;s<srcDialog.size();s++){
				txt.append("\t");
				if(matrix[tgtDialog.get(t)][srcDialog.get(s)].isNaN()){
					if(decFormat==null) txt.append(0.0);else txt.append("nc");
				}else{
					if(decFormat==null) txt.append(matrix[tgtDialog.get(t)][srcDialog.get(s)]);else txt.append(decFormat.format(matrix[tgtDialog.get(t)][srcDialog.get(s)]));
				}
			}
			txt.append("\r\n");
		}
		return txt.toString();
	}
	public void actionPerformed(ActionEvent e){}
}
