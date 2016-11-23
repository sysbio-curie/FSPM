package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
 */
import java.util.ArrayList;
import java.util.LinkedList;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
/**
 * Class which iterates internal classes through all paths
 * of the network as it is or if  back edges are open (OBE:open back edges)
 * When open back edge, no test of loop
 * It is an interruptible task
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class ComputingByPathTask extends ComputingThroughPath implements Task{
	boolean stop=false;
	TaskMonitor monitor;
	Class<? extends IterThroughPath> iterClass;
	public ComputingByPathTask(WeightGraphTools wgt,Class<? extends IterThroughPath> iterClass){
		super(wgt);
		this.iterClass=iterClass;
	}
	public void run(TaskMonitor monitor) throws Exception{
		this.monitor=monitor;
		monitor.setTitle("Number of Found Paths");
		if(iterClass==NodeListAll.class){
			NodeListAll iter=new NodeListAll();
			searchPaths(iter);
			return;}
		if(iterClass==NodeListOBE.class){
			NodeListOBE iter=new NodeListOBE();
			searchPathsOBE(iter);
			return;}
		if(iterClass==EdgeListAll.class){
			EdgeListAll iter=new EdgeListAll();
			searchPaths(iter);
			return;}
		if(iterClass==WeigthEdgeListAll.class){
			WeigthEdgeListAll iter=new WeigthEdgeListAll();
			searchPaths(iter);
			return;}
		if(iterClass==EdgeListOBE.class){
			EdgeListOBE iter=new EdgeListOBE();
			searchPathsOBE(iter);
			return;}
		if(iterClass==WeigthEdgeListOBE.class){
			WeigthEdgeListOBE iter=new WeigthEdgeListOBE();
			searchPathsOBE(iter);
			return;}
		if(iterClass==SignedDistanceAll.class){
			text.append("Source\tTarget\tSignedDistance-P-"+reach+"\r\n");
			SignedDistanceAll iter=new SignedDistanceAll();
			searchPaths(iter);
			return;}
		if(iterClass==InfluenceListAll.class){
			text.append("Source\tTarget\tInfluence-P-"+reach+"\r\n");
			InfluenceListAll iter=new InfluenceListAll();
			searchPaths(iter);
			return;}
		if(iterClass==InfluenceArrayAll.class){
			targetLine();
			InfluenceArrayAll iter=new InfluenceArrayAll();
			searchPaths(iter);
			return;}
	}
	int n;
	int src;
	int tgt;
	IterThroughPath iter;
	private void depthFirst(LinkedList<Integer> visited){
		ArrayList<Integer> edges = adjacency.get(visited.getLast());
		for (int edge:edges){
			int node=tgts.get(edge);
			if (!visited.contains(node)&&(node==tgt) ){
				visited.addLast(node);
				iter.doByPath(visited);
				monitor.setStatusMessage(Integer.toString(++n));
				visited.removeLast();
				break;
			}
		}
		if(stop) return;
		for (int edge:edges){
			int node=tgts.get(edge);
			if (!visited.contains(node)&&(node!=tgt)){
				visited.addLast(node);
				if(visited.size()<maxDepth) depthFirst(visited);
				visited.removeLast();
			}
		}
	}
	public void searchPaths(IterThroughPath iter){
		n=0;
		maxDepth=getMaxDepth();
		this.iter=iter;
		for(int s:sources){
			iter.begin(s);
			for(int t:targets){
				src=s;
				tgt=t;
				adjustAdjacency(src,tgt);
				LinkedList<Integer> visited = new LinkedList<Integer>();
				visited.add(src);
				depthFirst(visited);
				resetAdjacency();
			}
			iter.end();
		}
		if(stop) text.append("Stopped by User at "+n+" Paths");		
	}
	public void searchPathsOBE(IterThroughPath iter){
		n=0;
		maxDepth=getMaxDepth();
		this.iter=iter;
		for(int s:sources){
			iter.begin(s);
			adjacency=openLoopByDFS(s);
			for(int t:targets){
				src=s;
				tgt=t;
				LinkedList<Integer> visited = new LinkedList<Integer>();
				visited.add(src);
				depthFirst(visited);				
			}
			resetAdjacency();
			iter.end();
		}
		if(stop) text.append("Stopped by User at "+n+"Paths");
	}
	public void cancel(){
		stop=true;
	}
}
