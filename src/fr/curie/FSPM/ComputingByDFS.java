package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

import org.cytoscape.model.CyNetwork;
/**
 * Use depth first search to open the farthest back edges from source
 * Iterate along every path to 
 * case of multi path model
 * Compute influence and reach area in different configurations
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class ComputingByDFS extends ComputingThroughTree{
	int maxDepth;
	public ComputingByDFS(CyNetwork gp,int maxDepth) {
		super(gp);
		this.maxDepth=maxDepth;
	}
	public ComputingByDFS(WeightGraphStructure wgs, int maxDepth) {
		super();
		this.graph=wgs.graph;
		this.nodes=wgs.nodes;
		this.edges=wgs.edges;
		this.srcs=wgs.srcs;
		this.tgts=wgs.tgts;
		this.adjacency=wgs.adjacency;
		this.weights=wgs.weights;
		this.maxDepth=maxDepth;
	}	
	ArrayList<ArrayList<Integer>> openLoopByDFS(int root){
		ArrayList<LinkedList<Integer>> adjTmp=copyList(adjacency);
		ArrayList<ArrayList<Integer>> adjacent=new ArrayList<ArrayList<Integer>>();
		for(int n=0;n<nodes.size();n++) adjacent.add(new ArrayList<Integer>());
		Stack<Integer> nodeStack=new Stack<Integer>();
		Stack<Integer> levelStack=new Stack<Integer>();
		int depth=0;
		nodeStack.push(root);
		levelStack.push(depth);
		while((!nodeStack.isEmpty())&&(depth<maxDepth)){		
			int node=nodeStack.peek();
			depth=levelStack.peek();
			LinkedList<Integer> edges=adjTmp.get(node);			
			if((!edges.isEmpty())&(depth<maxDepth)){
				int edge=edges.remove();				
				if(!nodeStack.contains(tgts.get(edge))){
					adjacent.get(node).add(edge);
					depth++;			
					nodeStack.push(tgts.get(edge));
					levelStack.push(depth);
				}
			}else{
				node=nodeStack.pop();
				depth=levelStack.pop();
			}
		}
		return adjacent;
	}
	public ArrayList<Integer> backEdgesByDFS(int root,int maxDepth){
		ArrayList<LinkedList<Integer>> adjTmp=copyList(adjacency);
		ArrayList<Integer> edgeList=new ArrayList<Integer>();
		Stack<Integer> nodeStack=new Stack<Integer>();
		Stack<Integer> levelStack=new Stack<Integer>();
		int depth=0;
		nodeStack.push(root);
		levelStack.push(depth);
		while((!nodeStack.isEmpty())&&(depth<maxDepth)){			
			int node=nodeStack.peek();
			depth=levelStack.peek();
			LinkedList<Integer> edges=adjTmp.get(node);			
			if((!edges.isEmpty())&(depth<maxDepth)){
				int edge=edges.remove();				
				if(!nodeStack.contains(tgts.get(edge))){					
					depth++;			
					nodeStack.push(tgts.get(edge));
					levelStack.push(depth);
				}else edgeList.add(edge);
			}else{
				node=nodeStack.pop();
				depth=levelStack.pop();
			}
		}
		return edgeList;
	}

	void actThroughTree(int root,ArrayList<ArrayList<Integer>> adjacency,IterThroughTree iter){
		LinkedList<Integer> nodeQueue=new LinkedList<Integer>();
		nodeQueue.add(root);
		iter.init(root);
		while(!nodeQueue.isEmpty()){
			int node=nodeQueue.remove();
			iter.queueRemove(node);
			ArrayList<Integer> edges=adjacency.get(node);			
			for(int i=0;i<edges.size();i++){
				int edge=edges.get(i);
				nodeQueue.add(tgts.get(edge));
				iter.queueAdd(edge);
			}
		}
	}
	public ArrayList<Integer>[][] signedDistances(Collection<Integer> sources){
		SignedDistanceByITT d=new SignedDistanceByITT();
		ArrayList<ArrayList<Integer>> adj;		
		for(int s:sources){
			adj=openLoopByDFS(s);
			actThroughTree(s,adj,d);
		}
		return d.distance;
	}
	public Double[][] allInfluence(double fade,Collection<Integer> sources){
		InfluenceByITT i=new InfluenceByITT(fade);
		ArrayList<ArrayList<Integer>> adj;		
		for(int s:sources){
			adj=openLoopByDFS(s);
			actThroughTree(s,adj,i);
		}
		return i.influence;
	}
	public double[] activityFromIn(double fade,Collection<Integer> sources,double[] activIn){
		ActivityByITT a=new ActivityByITT(fade,activIn);
		ArrayList<ArrayList<Integer>> adj;		
		for(int s:sources){
			adj=openLoopByDFS(s);
			actThroughTree(s,adj,a);
		}
		double[] activOut=new double[nodes.size()];
		for(int i=0;i<nodes.size();i++){
			activOut[i]=0.0;
			for(int j=0;j<nodes.size();j++)activOut[i]=activOut[i]+a.activity[i][j];
		}
		return activOut;	
	}
	public Double[][] reachArea(double fade,Collection<Integer> sources){
		ReachAreaByITT ra=new ReachAreaByITT(fade);
		ArrayList<ArrayList<Integer>> adj;		
		for(int s:sources){
			adj=openLoopByDFS(s);
			actThroughTree(s,adj,ra);
		}
		return ra.area;
	}
	public double[] reachAreaFromStarts(double fade,Collection<Integer> sources,double[] startNodes){
		Double[][] reachArea=reachArea(fade,sources);
		double[] inflOnNodes=new double[nodes.size()];
		for(int t=0;t<nodes.size();t++) {
			inflOnNodes[t]=0.0;
			for(int s=0;s<nodes.size();s++) inflOnNodes[t]=inflOnNodes[t]+startNodes[s]*reachArea[t][s];
		}
		return inflOnNodes;
	}
}
