package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE  
 */
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
/**
 * Interruptible task, test effect by blocking every node
 * Warns if change is higher than threshold
 * A blocked node has no downstream edge
 * Use the adjacency to modify the network 
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class BlockingNodeThresholdTask implements Task{
	private boolean stop=false;
	ComputingByTreeTask ctt;
	double threshold;
	protected final String formatLabel="0.000000";
	protected int discretize(Double threshold,Double value){
		if(value>threshold) return 1;
		if(value<-threshold) return -1;
		if(value.isNaN()) return 0;
		return 0;
	}
	public BlockingNodeThresholdTask(ComputingByTreeTask ctt,double threshold){
		this.ctt=ctt;
		this.threshold=threshold;
	}
	public void run(TaskMonitor monitor) throws Exception {
		DecimalFormat decFormat=new DecimalFormat(formatLabel);
		Double[][] influence=ctt.allInfluence();
		int[][] dInfluence=new int[ctt.nodes.size()][ctt.nodes.size()];
		for(int s=0;s<ctt.nodes.size();s++)for(int t=0;t<ctt.nodes.size();t++) dInfluence[t][s]=discretize(threshold,influence[t][s]);
		ctt.text.append("Source\tTarget\tStart Infl\tStart Real\tNew Infl By\tNew Real By\tBlockingNode\r\n");
		monitor.setTitle("Progress of computing node by node");
		double progress=0;
		double step=1.0/ctt.nodes.size();
		int percent=0;
		ArrayList<Integer> empty=new ArrayList<Integer>();
			for(int node=0;node<ctt.nodes.size();node++){
			if(stop){
				ctt.text.append("Stopped by user at "+percent+"%");
				return;
			}
			ArrayList<Integer> keptList=ctt.adjacency.get(node);
			ctt.adjacency.set(node,empty);
			Double[][] matrix=ctt.allInfluence();
			boolean isChanged=false;
			for(int s=0;s<ctt.sources.size();s++)for(int t=0;t<ctt.targets.size();t++){
				int di=discretize(threshold,matrix[ctt.targets.get(t)][ctt.sources.get(s)]);
				if(di!=dInfluence[ctt.targets.get(t)][ctt.sources.get(s)]){
					ctt.text.append(ctt.getName(ctt.nodes.get(ctt.sources.get(s))));
					ctt.text.append("\t");
					ctt.text.append(ctt.getName(ctt.nodes.get(ctt.targets.get(t))));
					ctt.text.append("\t");
					ctt.text.append(Integer.toString(dInfluence[ctt.targets.get(t)][ctt.sources.get(s)]));
					ctt.text.append("\t");
					if(influence[ctt.targets.get(t)][ctt.sources.get(s)].isNaN()) ctt.text.append(formatLabel);
					else ctt.text.append(decFormat.format(influence[ctt.targets.get(t)][ctt.sources.get(s)]).toString());
					ctt.text.append("\t");
					ctt.text.append(Integer.toString(di));
					ctt.text.append("\t");						
					if(matrix[ctt.targets.get(t)][ctt.sources.get(s)].isNaN()) ctt.text.append(formatLabel);
					else ctt.text.append(decFormat.format(matrix[ctt.targets.get(t)][ctt.sources.get(s)]));
					ctt.text.append("\t");
					ctt.text.append(ctt.getName(ctt.nodes.get(node)));
					ctt.text.append("\r\n");
					isChanged=true;
				}				
			}
			if(!isChanged){
				ctt.text.append("AllSources\tAllTargets\tX\tX\tNoChange\tNoChange\t");
				ctt.text.append(ctt.getName(ctt.nodes.get(node)));
				ctt.text.append("\r\n");
			}
			ctt.adjacency.set(node,keptList);
			monitor.setProgress(progress=progress+step);
			percent=(int)(progress*100);
			monitor.setStatusMessage(percent+"%");
		}
		return;	
	}
	public void cancel() {
		stop=true;		
	}
}
