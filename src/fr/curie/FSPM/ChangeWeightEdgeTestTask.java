package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
/**
 * Test change of weight (=0 or reverse by overriding change and restore in ComputeKappa)
 * or open edge (by change sub-array of adjacency and restore it)
 * Act only on edges between nodes where activity is not zero (what edges)
 * If not cancel, display results sorted by kappa
 * @author Daniel.Rovera@curie.fr or @mail.com
 */
public class ChangeWeightEdgeTestTask extends AbstractTask{
	private boolean stop=false;
	ComputeKappa ck;
	String title;
	ArrayList<Integer> foundEdges;
	ArrayList<Double> kappas;
	DecimalFormat decFormat;
	public ChangeWeightEdgeTestTask(ComputeKappa ck,String title){
		this.ck=ck;
		this.title=title;
		decFormat=new DecimalFormat(ck.formatLabel);
	}
	public void insertionSort(ArrayList<Double> keys,ArrayList<Integer> values){
		for (int i=1;i<keys.size();i++){
			int j=i;
			Double ti=keys.get(i);
			int ts=values.get(i);
			while ((j > 0) && (keys.get(j-1)>ti)){
				keys.set(j,keys.get(j-1));
				values.set(j,values.get(j-1));
				j--;
			}
			keys.set(j,ti);
			values.set(j,ts);
		}
	}
	private void addLine(int index,boolean inTask){
		ck.txt.append(ck.wgs.getName(ck.wgs.edges.get(foundEdges.get(index))));
		ck.txt.append("\t");
		ck.txt.append(ck.weightValue(foundEdges.get(index),inTask));
		ck.txt.append("\t");
		ck.txt.append(decFormat.format(kappas.get(index)).toString());
		ck.txt.append("\r\n");
	}	
	public void run(TaskMonitor monitor){
		monitor.setTitle("Show results as they are found");
		ArrayList<Integer> activSrc=new ArrayList<Integer>();
		ArrayList<Integer> activTgt=new ArrayList<Integer>();
		for(int j=0;j<ck.inputKeyColName.keySet().size();j++){
			for(int i=0;i<ck.wgs.nodes.size();i++){
				if(ck.activIn[j][i]!=0) activSrc.add(i);
				if(ck.activAim[j][i]!=0) activTgt.add(i);
			}
		}
		HashSet<Integer> whatEdges=(new WeightGraphStructure(ck.wgs.net)).extractEdges(activSrc,activTgt);
		double startKappa=ck.getKappa();			
		ck.txt.append("startKappa=\t\t");ck.txt.append(Double.toString(startKappa));ck.txt.append("\r\n");
		ck.txt.append("Edge\tWeight\tKappa\r\n");
		foundEdges=new ArrayList<Integer>();
		kappas=new ArrayList<Double>();
		double progress=0.0;
		double step=1.0/whatEdges.size();
		int percent=0;		
		for(int edge:whatEdges){
			if(stop){
				ck.txt.append("Stopped by user at "+percent+"%");
				return;
			}
			ck.change(edge);
			double kappa=ck.getKappa();
			if(kappa>startKappa){
				foundEdges.add(edge);
				kappas.add(kappa);
				addLine(foundEdges.size()-1,true);
			}
			ck.restore(edge);
			monitor.setProgress(progress=progress+step);
			percent=(int)(progress*100);
			monitor.setStatusMessage(percent+"%");			
		}
		monitor.setStatusMessage("Decreasing Kappa List");
		ck.txt.set(ck.params());
		insertionSort(kappas,foundEdges);
		ck.txt.append("startKappa=\t\t");ck.txt.append(Double.toString(startKappa));ck.txt.append("\r\n");
		ck.txt.append("Edge\tWeight\tKappa\r\n");
		for(int i=kappas.size()-1;i>-1;i--) addLine(i,false);
	}		
	public void cancel(){
		stop=true;
	}
}
