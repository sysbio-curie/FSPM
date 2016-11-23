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
 * Interruptible task, test effect of every edge by opening it
 * Warn if change is higher than threshold
 * use the adjacency to modify the network
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class OpeningEdgeThresholdTask implements Task{
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
	public OpeningEdgeThresholdTask(ComputingByTreeTask ctt,double threshold){
		this.ctt=ctt;
		this.threshold=threshold;
	}
	public void run(TaskMonitor monitor) throws Exception {
		DecimalFormat decFormat=new DecimalFormat(formatLabel);	
		Double[][] influence=ctt.allInfluence();
		int[][] dInfluence=new int[ctt.nodes.size()][ctt.nodes.size()];
		for(int s=0;s<ctt.nodes.size();s++)for(int t=0;t<ctt.nodes.size();t++) dInfluence[t][s]=discretize(threshold,influence[t][s]);
		ctt.text.append("Source\tTarget\tStart Infl\tStart Real\tNew Infl By\tNew Real By\tBlockingEdge\r\n");
		monitor.setTitle("Progress of computing edge by edge");
		double progress=0;
		double step=1.0/ctt.edges.size();
		int percent=0;
		for(int edge=0;edge<ctt.edges.size();edge++){
			if(stop){
				ctt.text.append("Stopped by user at "+percent+"%");
				return;
			}
			if(!ctt.adjacency.get(ctt.srcs.get(edge)).isEmpty()){
				ArrayList<Integer> keptList=ctt.adjacency.get(ctt.srcs.get(edge));
				ArrayList<Integer> withoutEdge=new ArrayList<Integer>(keptList);
				withoutEdge.remove((Integer)edge);
				ctt.adjacency.set(ctt.srcs.get(edge),withoutEdge);
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
						ctt.text.append(ctt.getName(ctt.edges.get(edge)));
						ctt.text.append("\r\n");
						isChanged=true;
					}
				}
				if(!isChanged){
					ctt.text.append("AllSources\tAllTargets\tX\tX\tNoChange\tNoChange\t");
					ctt.text.append(ctt.getName(ctt.edges.get(edge)));
					ctt.text.append("\r\n");
				}
				ctt.adjacency.set(ctt.srcs.get(edge),keptList);
				monitor.setProgress(progress=progress+step);
				percent=(int)(progress*100);
				monitor.setStatusMessage(percent+"%");
			}
		}	
		return;
	}
	public void cancel() {
		stop=true;		
	}
}

