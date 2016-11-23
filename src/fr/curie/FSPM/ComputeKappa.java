package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
/**
 * Extract data sets from attributes : input and aim from observations
 * Root names  (...ID) of attribute have the same length
 * Check if every input set correspond to one aim set by the same number
 * Are included only nodes where input activity is different from 0
 * Compare results of computing to observations using a threshold
 * Compute score on all observations
 * Create the corresponding Task Factory
 * @author Daniel.Rovera@Curie.fr or @gmail.com
 *
 */
public class ComputeKappa {
	double threshold;
	final String title="Computing Kappa ";
	final String aimError="Cannot match sets to aims or attribute type is not floating\r\n";
	final int lengthID=10;
	final String inputSetID= "INPUT__SET";
	final String outputAimID="OUTPUT_AIM";
	protected ModelMenuUtils menuUtils;
	protected JFrame frame;
	protected TreeMap<String,String> inputKeyColName;
	protected TreeMap<String,String> aimKeyColName;
	protected WeightGraphStructure wgs;
	protected double[][] activIn;
	protected double[][] activAim;
	protected int[] activOk;
	protected int[] activNo;
	protected int[] inhibOk;
	protected int[] inhibNo;
	protected final String formatLabel="0.000000";
	protected TextBox txt;
	protected void change(int edge){}
	protected void restore(int edge){}
	String weightValue(int edge,boolean inTask){return wgs.weights.get(edge).toString();}
	// Not used but useful for checking the data sets
	protected void displayLog(int set,double[] activOut){
		for(int n=0;n<activAim[set].length;n++){
			if(activAim[set][n]!=0){
				txt.append(wgs.net.getRow(wgs.nodes.get(n)).get(CyNetwork.NAME, String.class));
				txt.append("\t");
				txt.append(Double.toString(activAim[set][n]));
				txt.append("\t");
				txt.append(Double.toString(activOut[n]));
				txt.append("\r\n");
			}
		}
	}
	public ComputeKappa(CyNetwork network,JFrame frame){
		wgs=new WeightGraphStructure(network);	
		menuUtils = new ModelMenuUtils(wgs,frame);
		menuUtils.srcDialog=new ArrayList<Integer>();
		menuUtils.tgtDialog=new ArrayList<Integer>();				
		if(!wgs.initWeights()){
			JOptionPane.showMessageDialog(frame,menuUtils.errorWeigth,title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		wgs.reach=menuUtils.getReach();
		if(wgs.reach==0.0) return;	
		threshold=menuUtils.getThreshold();
		if(threshold==Double.MAX_VALUE) return;		
		txt=new TextBox(frame,title+" "+wgs.getName(),0.5,1.0);
		txt.setVisible(true);
		txt.append(params());
	}
	protected String params(){
		StringBuffer text=new StringBuffer();
		text.append("Reach=");text.append(wgs.reach);	
		text.append("/ScoreThreshold=");text.append(threshold);
		text.append("\r\n");
		return text.toString();
	}
	boolean getSetData(){
		inputKeyColName=new TreeMap<String,String>();
		aimKeyColName=new TreeMap<String,String>();
		Collection<CyColumn> columnList = wgs.net.getDefaultNodeTable().getColumns();		
		CyColumn col = null;
		Iterator<CyColumn> iter = columnList.iterator();		
		while(iter.hasNext()) {
			col=iter.next();
			String colName;
			if(col.getType()!=Double.class) continue; else colName=col.getName();
			if(colName.length()>lengthID){
				if(colName.substring(0,lengthID).equals(inputSetID)) inputKeyColName.put(colName.substring(lengthID),colName);
				if(colName.substring(0,lengthID).equals(outputAimID)) aimKeyColName.put(colName.substring(lengthID),colName);
			}
		}							
		if(inputKeyColName.isEmpty()) return false;
		Iterator<String> setIt=inputKeyColName.keySet().iterator();
		Iterator<String> aimIt=aimKeyColName.keySet().iterator();
		while(setIt.hasNext()&aimIt.hasNext()) if(!setIt.next().equals(aimIt.next()))return false;		
		activIn=new double[inputKeyColName.keySet().size()][];			
		activAim=new double[aimKeyColName.keySet().size()][];
		int set=0;
		for(String key:inputKeyColName.keySet()){
			activIn[set]=new double[wgs.nodes.size()];
			for(int n=0;n<wgs.nodes.size();n++){
				Double d = wgs.net.getRow(wgs.nodes.get(n)).get(inputKeyColName.get(key),Double.class);
				if(d!=null){
					activIn[set][n]=d;
					if(d!=0.0) menuUtils.srcDialog.add(n);
				}else activIn[set][n]=0.0;
			}
			activAim[set]=new double[wgs.nodes.size()];
			for(int n=0;n<wgs.nodes.size();n++){
				Double d = wgs.net.getRow(wgs.nodes.get(n)).get(aimKeyColName.get(key),Double.class);
				if(d!=null)	activAim[set][n]=d;else activAim[set][n]=0.0;
			}
			set++;
		}
		activOk=new int[inputKeyColName.keySet().size()];
		activNo=new int[inputKeyColName.keySet().size()];
		inhibOk=new int[inputKeyColName.keySet().size()];
		inhibNo=new int[inputKeyColName.keySet().size()];		
		return true;
	}
	public void displayScore(){
		DecimalFormat decFormat=new DecimalFormat(formatLabel);
		txt.append("Set\tInput Set Size\tOutput Aim Size\tSign Score\tActive Ok\tInhibit Ok\tKappa\r\n");
		int activOkSum=0;
		int activNoSum=0;
		int inhibOkSum=0;
		int inhibNoSum=0;
		Iterator<String> iter=inputKeyColName.keySet().iterator();
		for(int s=0;s<inputKeyColName.keySet().size();s++){
			stateOkNo(s);
			activOkSum=activOkSum+activOk[s];
			activNoSum=activNoSum+activNo[s];
			inhibOkSum=inhibOkSum+inhibOk[s];
			inhibNoSum=inhibNoSum+inhibNo[s];
			txt.append(iter.next());txt.append("\t");
			int setNb=0;for(int n=0;n<wgs.nodes.size();n++) if(activIn[s][n]!=0.0) setNb++;
			txt.append(Integer.toString(setNb));txt.append("\t");
			txt.append(Integer.toString(activOk[s]+activNo[s]+inhibOk[s]+inhibNo[s]));txt.append("\t");
			txt.append(Integer.toString(activOk[s]+inhibOk[s]));txt.append("\t");
			txt.append(Integer.toString(activOk[s]));txt.append("\t");
			txt.append(Integer.toString(inhibOk[s]));txt.append("\t");
			txt.append(decFormat.format(kappa(activOk[s],activNo[s],inhibOk[s],inhibNo[s])).toString());txt.append("\r\n");
		}
		txt.append("\t\t");
		txt.append(Integer.toString(activOkSum+activNoSum+inhibOkSum+inhibNoSum));txt.append("\t");
		txt.append(Integer.toString(activOkSum+inhibOkSum));txt.append("\t");
		txt.append(Integer.toString(activOkSum));txt.append("\t");
		txt.append(Integer.toString(inhibOkSum));txt.append("\t");
		txt.append(decFormat.format(kappa(activOkSum,activNoSum,inhibOkSum,inhibNoSum)).toString());
	}
	double kappa(int activOk,int activNo,int inhibOk, int inhibNo){
		double n=activOk+activNo+inhibOk+inhibNo;
		double ok=(double)(activOk+inhibOk)/n;
		double rk=(double)((activOk+activNo)*(activOk+inhibNo)+(inhibOk+activNo)*(inhibOk+inhibNo))/n/n;
		return (ok-rk)/(1.0-rk);
	}
	void stateOkNo(int set){
		activOk[set]=0;
		activNo[set]=0;
		inhibOk[set]=0;
		inhibNo[set]=0;
		double[] activOut;
		ComputingByTreeTask cpt=new ComputingByTreeTask(new WeightGraphTools(wgs,menuUtils.srcDialog,null,null));
		activOut=cpt.activityFromIn(activIn[set]);
		for(int n=0;n<wgs.nodes.size();n++){
			if(activAim[set][n]>0){
				if(activOut[n]>threshold) activOk[set]++; else activNo[set]++; 
			}else{
				if(activAim[set][n]<0){
					if(activOut[n]<-threshold) inhibOk[set]++; else inhibNo[set]++;
				}
			}
		}
	}
	double getKappa(){
		int activOkSum=0;
		int activNoSum=0;
		int inhibOkSum=0;
		int inhibNoSum=0;
		for(int s=0;s<inputKeyColName.keySet().size();s++){
			stateOkNo(s);
			activOkSum=activOkSum+activOk[s];
			activNoSum=activNoSum+activNo[s];
			inhibOkSum=inhibOkSum+inhibOk[s];
			inhibNoSum=inhibNoSum+inhibNo[s];			
		}
		return kappa(activOkSum,activNoSum,inhibOkSum,inhibNoSum);
	}
	public class KappaTaskFactory extends AbstractTaskFactory{
		ComputeKappa ck;
		String title;
		public KappaTaskFactory(ComputeKappa ck,String title){
			this.ck=ck;
			this.title=title;
		}
		public TaskIterator createTaskIterator() {
			return(new TaskIterator((new ChangeWeightEdgeTestTask(ck,title))));
		}		
	}
}
