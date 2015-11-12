package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2013 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList; 
import org.cytoscape.model.CyNetwork;
/**
 * Use breadth first search to iterate class for computing
 * Case of mono path model
 * list of edges between nodes 
 * Reverse breadth first search is used by extract edges between nodes
 * the shortest path matrix used in clustering
 * Computing influence and reach area in different configurations
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class ComputingByBFS extends ComputingThroughTree{
	
	
	public ComputingByBFS(CyNetwork gp) {
		super(gp);
	}
	
	public ComputingByBFS(WeightGraphStructure wgs) {
		super();
		this.graph=wgs.graph;
		this.nodes=wgs.nodes;
		this.edges=wgs.edges;
		this.srcs=wgs.srcs;
		this.tgts=wgs.tgts;
		this.adjacency=wgs.adjacency;
		this.weights=wgs.weights;		
	}
	void actThroughTree(int root,ArrayList<ArrayList<Integer>> adjacency,IterThroughTree iter){
		LinkedList<Integer> nodeQueue=new LinkedList<Integer>();
		nodeQueue.add(root);
		iter.init(root);
		while(!nodeQueue.isEmpty()){
			int node=nodeQueue.remove();
			iter.queueRemove(node);
			for(int i=0;i<adjacency.get(node).size();i++){
				int edge=adjacency.get(node).get(i);
				nodeQueue.add(tgts.get(edge));
				iter.queueAdd(edge);
			}
			adjacency.get(node).clear();
		}
	}
	public ArrayList<Integer>[][] signedDistances(Collection<Integer> sources){
		SignedDistanceByITT d=new SignedDistanceByITT();		
		for(int s:sources){
			actThroughTree(s,copyArray(adjacency),d);
		}
		return d.distance;
	}
	public Double[][] allInfluence(double fade,Collection<Integer> sources){
		InfluenceByITT i=new InfluenceByITT(fade);	
		for(int s:sources){
			actThroughTree(s,copyArray(adjacency),i);
		}
		return i.influence;
	}
	public double[] activityFromIn(double fade,Collection<Integer> sources,double[] activIn){
		ActivityByITT a=new ActivityByITT(fade,activIn);		
		for(int s:sources){
			actThroughTree(s,copyArray(adjacency),a);
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
		for(int s:sources){
			actThroughTree(s,copyArray(adjacency),ra);
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
	public HashSet<Integer> extractNodes(ArrayList<Integer> srcList,ArrayList<Integer> tgtList){
		EdgesByBFS direct=new EdgesByBFS(edges.size()/2);
		for(int src:srcList) actThroughTree(src,copyArray(adjacency), direct);
		HashSet<Integer> nodesByRevers=new HashSet<Integer>(nodes.size()/2);
		ArrayList<ArrayList<Integer>> reverseAdj=new ArrayList<ArrayList<Integer>>(nodes.size());
		for(int i=0;i<nodes.size();i++) reverseAdj.add(new ArrayList<Integer>());
		for(int i=0;i<edges.size();i++) if(direct.edges.contains(i)) reverseAdj.get(tgts.get(i)).add(i);
		for(int tgt:tgtList){
			LinkedList<Integer> nodeQueue=new LinkedList<Integer>();
			ArrayList<ArrayList<Integer>> adjTmp=copyArray(reverseAdj);
			nodeQueue.add(tgt);
			nodesByRevers.add(tgt);
			while(!nodeQueue.isEmpty()){
				int node=nodeQueue.remove();
				for(int i=0;i<adjTmp.get(node).size();i++){
					int edge=adjTmp.get(node).get(i);
					nodeQueue.add(srcs.get(edge));
					nodesByRevers.add(srcs.get(edge));				
				}
				adjTmp.get(node).clear();
			}
		}
		return nodesByRevers;
	}
	public HashSet<Integer> extractEdges(HashSet<Integer> srcList,HashSet<Integer> tgtList){
		EdgesByBFS direct=new EdgesByBFS(srcs.size()/2);
		for(int src:srcList) actThroughTree(src,copyArray(adjacency), direct);
		HashSet<Integer> edgesByRevers=new HashSet<Integer>();
		ArrayList<ArrayList<Integer>> reverseAdj=new ArrayList<ArrayList<Integer>>(nodes.size());
		for(int i=0;i<nodes.size();i++) reverseAdj.add(new ArrayList<Integer>());
		for(int i=0;i<srcs.size();i++) if(direct.edges.contains(i)) reverseAdj.get(tgts.get(i)).add(i);
		for(int tgt:tgtList){
			LinkedList<Integer> nodeQueue=new LinkedList<Integer>();
			ArrayList<ArrayList<Integer>> adjTmp=copyArray(reverseAdj);
			nodeQueue.add(tgt);
			while(!nodeQueue.isEmpty()){
				int node=nodeQueue.remove();
				for(int i=0;i<adjTmp.get(node).size();i++){
					int edge=adjTmp.get(node).get(i);
					nodeQueue.add(srcs.get(edge));
					edgesByRevers.add(edge);
				}
				adjTmp.get(node).clear();
			}
		}
		return edgesByRevers;
	}
	public int[][] shortPathMatrix(){
		ShortPathByBFS sp=new ShortPathByBFS();
		for(int root=0;root<nodes.size();root++) actThroughTree(root,copyArray(adjacency),sp);
		return sp.spMx;
	}
}
