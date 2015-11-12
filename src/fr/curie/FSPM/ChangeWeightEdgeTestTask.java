package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.util.ArrayList;
import java.util.HashSet;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
/**
 * Test change of weight (=0 or reverse by overriding changeWeight in ComputeKappa)
 * Act only on edges between nodes where activity is not zero (what edges)
 * 
 * @author Daniel.Rovera@curie.fr
 *
 */
public class ChangeWeightEdgeTestTask extends AbstractTask{
	ComputeKappa ck;
	String title;
	public ChangeWeightEdgeTestTask(ComputeKappa ck,String title){this.ck=ck;this.title=title;}
	protected void displayLog(int edge,double keptWeight,double kappa){
		ck.txt.append(ck.net.getRow(ck.wgs.edges.get(edge)).get(CyNetwork.NAME, String.class));
		ck.txt.append("\t");
		ck.txt.append(keptWeight);
		ck.txt.append(">");
		ck.txt.append(ck.wgs.weights.get(edge));
		ck.txt.append("\t");
		ck.txt.append(kappa);
		ck.txt.append("\r\n");
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
	public void run(TaskMonitor taskMonitor){
		taskMonitor.setTitle(title);
		HashSet<Integer> activSrc=new HashSet<Integer>();
		HashSet<Integer> activTgt=new HashSet<Integer>();
		for(int j=0;j<ck.inputKeyColName.keySet().size();j++){
			for(int i=0;i<ck.wgs.nodes.size();i++){
				if(ck.activIn[j][i]!=0) activSrc.add(i);
				if(ck.activAim[j][i]!=0) activTgt.add(i);
			}
		}
		HashSet<Integer> whatEdges=(new ComputingByBFS(ck.wgs)).extractEdges(activSrc,activTgt);
		double startKappa=ck.getKappa();			
		ck.txt.append("startKappa=\t\t");ck.txt.append(startKappa);ck.txt.append("\r\n");
		ck.txt.append("Edge\tWeight\tKappa\r\n");
		ArrayList<Integer> foundEdges=new ArrayList<Integer>();
		ArrayList<Double> kappas=new ArrayList<Double>();
		int c=0;
		for(int edge:whatEdges){
			double keptWeight=ck.wgs.weights.get(edge);
			ck.changeWeight(edge);
			double kappa=ck.getKappa();
			if(kappa>startKappa){
				foundEdges.add(edge);
				kappas.add(kappa);																
			}
			ck.wgs.weights.set(edge,keptWeight);
			taskMonitor.setProgress(1*c++/whatEdges.size());			
		}
		insertionSort(kappas,foundEdges);
		for(int i=kappas.size()-1;i>-1;i--){
			ck.txt.append(ck.net.getRow(ck.wgs.edges.get(foundEdges.get(i))).get(CyNetwork.NAME, String.class));
			ck.txt.append("\t");
			ck.txt.append(ck.weightValue(foundEdges.get(i)));
			ck.txt.append("\t");
			ck.txt.append(kappas.get(i));
			ck.txt.append("\r\n");
		}
		taskMonitor.setStatusMessage("...Completed");
		new TextBox(ck.frame,title,ck.txt.toString()).setVisible(true);
	}		
	public void cancel(){}
}
