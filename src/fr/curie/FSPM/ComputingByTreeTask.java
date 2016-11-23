package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.util.ArrayList;
import java.util.LinkedList;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
/**
 * After opening back edges iterate along every tree to compute influence and reach area
 * iterClass is iterated by ThroughTrees which can be interruptible or not,
 * choice depending on the call context  
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class ComputingByTreeTask extends ComputingThroughTree implements Task{
	boolean stop=false;
	TaskMonitor monitor;
	Class<? extends IterThroughTree> iterClass;
	public ComputingByTreeTask(WeightGraphTools wgt,Class<? extends IterThroughTree> iterClass) {
		super(wgt);
		this.iterClass=iterClass;
	}
	public ComputingByTreeTask(WeightGraphTools wgt){super(wgt);}
	public void run(TaskMonitor monitor) throws Exception{
		this.monitor=monitor;
		monitor.setTitle("Done Sources");
		if(iterClass==SignedDistancList.class){
			text.append("Source\tTarget\tSignedDistance-M-"+reach+"\r\n");
			SignedDistancList iter=new SignedDistancList();
			ThroughTrees(iter);
			return;
		}
		if(iterClass==SignedDistance.class){
			SignedDistance iter=new SignedDistance();
			targetLine();
			ThroughTrees(iter);
			return;
		}
		if(iterClass==InfluenceComp.class){
			InfluenceComp iter=new InfluenceComp();
			targetLine();
			ThroughTrees(iter);
			return;
		}
		if(iterClass==InfluenceVisu.class){
			InfluenceVisu iter=new InfluenceVisu();
			targetLine();
			ThroughTrees(iter);
			return;
		}
		if(iterClass==InfluenceList.class){
			text.append("Source\tTarget\tInfluence-M-"+reach+"\r\n");
			InfluenceList iter=new InfluenceList();
			ThroughTrees(iter);
			return;
		}
		if(iterClass==ReachAreaText.class){
			ReachAreaText iter=new ReachAreaText();
			targetLine();
			ThroughTrees(iter);
			return;
		}
	}
	void ThroughTrees(IterThroughTree iter){
		ArrayList<ArrayList<Integer>> adj;
		LinkedList<Integer> nodeQueue=new LinkedList<Integer>();
		int n=0;
		for(int root:sources){
			adj=openLoopByDFS(root);			
			nodeQueue.add(root);
			iter.init(root);
			while(!nodeQueue.isEmpty()){
				int node=nodeQueue.remove();
				iter.queueRemove(node);
				ArrayList<Integer> edges=adj.get(node);			
				for(int i=0;i<edges.size();i++){
					int edge=edges.get(i);
					nodeQueue.add(tgts.get(edge));
					iter.queueAdd(edge);
				}
			}
			monitor.setProgress(((double)++n)/sources.size());
			monitor.setStatusMessage(n+"/"+sources.size());;
			if(stop){text.append("Stopped by User at "+n+"/"+sources.size()+" sources");return;}	
			iter.end();
		}		
	}
	public void cancel() {
		stop=true;
	}
	void noStopThroughTrees(IterThroughTree iter){
		ArrayList<ArrayList<Integer>> adj;
		LinkedList<Integer> nodeQueue=new LinkedList<Integer>();
		for(int root:sources){
			adj=openLoopByDFS(root);			
			nodeQueue.add(root);
			iter.init(root);
			while(!nodeQueue.isEmpty()){
				int node=nodeQueue.remove();
				iter.queueRemove(node);
				ArrayList<Integer> edges=adj.get(node);			
				for(int i=0;i<edges.size();i++){
					int edge=edges.get(i);
					nodeQueue.add(tgts.get(edge));
					iter.queueAdd(edge);
				}
			}
			iter.end();
		}		
	}
	public Double[][] allInfluence(){
		InfluenceOnly i=new InfluenceOnly();
		noStopThroughTrees(i);
		return i.influence;
	}
	public double[] activityFromIn(double[] activIn){
		ActivityByITT a=new ActivityByITT(activIn);
		noStopThroughTrees(a);
		double[] activOut=new double[nodes.size()];
		for(int i=0;i<nodes.size();i++){
			activOut[i]=0.0;
			for(int j=0;j<nodes.size();j++)activOut[i]=activOut[i]+a.activity[i][j];
		}
		return activOut;	
	}
	public double[] reachAreaFromStarts(double[] startNodes){
		ReachAreaOnly ra=new ReachAreaOnly();
		noStopThroughTrees(ra);
		Double[][] reachArea=ra.area;
		double[] inflOnNodes=new double[nodes.size()];
		for(int t=0;t<nodes.size();t++) {
			inflOnNodes[t]=0.0;
			for(int s=0;s<nodes.size();s++) inflOnNodes[t]=inflOnNodes[t]+startNodes[s]*reachArea[t][s];
		}
		return inflOnNodes;
	}
}
