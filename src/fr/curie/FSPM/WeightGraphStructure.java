package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
/**
 * Basis class for all about graph
 * Put the graph from Cytoscape network in practical data structure:
 * arrays of nodes, edges, sources, targets and adjacency
 * Sort nodes and edges by identifier
 * get nodes and edges between 2 sets of nodes by intersection of 2 sets got 
 * by descending and ascending the graph
 * Practical functions about copy and names
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class WeightGraphStructure {
	public CyNetwork net;
	public ArrayList<CyNode> nodes;
	public ArrayList<CyEdge> edges;
	public ArrayList<Integer> srcs;
	public ArrayList<Integer> tgts;
	public ArrayList<Double> weights;
	public ArrayList<ArrayList<Integer>> adjacency;
	public double reach;
	final String influenceAttr="WEIGHT";
	public WeightGraphStructure(){}
	public WeightGraphStructure(CyNetwork gp){
		cyLoad(gp);
		doStructure(gp);
		reach=Double.MAX_VALUE;
		weights=null;
	}
	public WeightGraphStructure(CyNetwork gp,double reach){
		cyLoad(gp);
		doStructure(gp);
		this.reach=reach;
		weights=null;		
	}
	public WeightGraphStructure(WeightGraphStructure wgs){
		this.net=wgs.net;
		this.nodes=wgs.nodes;
		this.edges=wgs.edges;
		this.srcs=wgs.srcs;
		this.tgts=wgs.tgts;
		this.weights=wgs.weights;
		this.adjacency=wgs.adjacency;
		this.reach=wgs.reach;
	}
	void cyLoad(CyNetwork gp){
		net=gp;
		nodes=new ArrayList<CyNode>(net.getNodeList());
		edges=new ArrayList<CyEdge>(net.getEdgeList());
	}
	public boolean initWeights(){
		weights=new ArrayList<Double>(edges.size());
		for(int i=0;i<edges.size();i++){
			Double weight=net.getRow(edges.get(i)).get(influenceAttr,Double.class);
			if(weight!=null) 
				weights.add(weight); 
			else 
				return false;
		}
		return true;
	}
	void doStructure(final CyNetwork net){
		srcs=new ArrayList<Integer>(edges.size());
		tgts=new ArrayList<Integer>(edges.size());
		adjacency=new ArrayList<ArrayList<Integer>>(nodes.size());
		Collections.sort(nodes,new Comparator<CyNode>(){
			public int compare(CyNode n1, CyNode n2){
				return net.getRow(n1).get(CyNetwork.NAME, String.class).compareTo(net.getRow(n2).get(CyNetwork.NAME, String.class));
		}});
		Collections.sort(edges,new Comparator<CyEdge>(){
			public int compare(CyEdge n1, CyEdge n2){
				return net.getRow(n1).get(CyNetwork.NAME, String.class).compareTo(net.getRow(n2).get(CyNetwork.NAME, String.class));
		}});
		for(int i=0;i<nodes.size();i++) adjacency.add(new ArrayList<Integer>());
		for(int e=0;e<edges.size();e++){	
			int src=nodes.indexOf(edges.get(e).getSource());
			srcs.add(src);
			adjacency.get(src).add(e);
			tgts.add(nodes.indexOf(edges.get(e).getTarget()));
		}	
	}
	protected ArrayList<LinkedList<Integer>> copyList(ArrayList<ArrayList<Integer>> old_){
		ArrayList<LinkedList<Integer>> new_=new ArrayList<LinkedList<Integer>>(old_.size());
		for(int n=0;n<old_.size();n++) new_.add(n,new LinkedList<Integer>(old_.get(n)));
		return new_;
	}
	protected ArrayList<ArrayList<Integer>> copyArray(ArrayList<ArrayList<Integer>> old_){
		ArrayList<ArrayList<Integer>> new_=new ArrayList<ArrayList<Integer>>(old_.size());
		for(int n=0;n<old_.size();n++) new_.add(n,new ArrayList<Integer>(old_.get(n)));
		return new_;
	}
	public void adjustAdjacency(ArrayList<Integer> srcList,ArrayList<Integer> tgtList){
		int[] passed=new int[edges.size()];
		for(int i=0;i<edges.size();i++) passed[i]=0;
		ArrayList<ArrayList<Integer>> reverseAdj=new ArrayList<ArrayList<Integer>>(nodes.size());
		for(int i=0;i<nodes.size();i++) reverseAdj.add(new ArrayList<Integer>());
		for(int i=0;i<srcs.size();i++) reverseAdj.get(tgts.get(i)).add(i);
		LinkedList<Integer> nodeQueue=new LinkedList<Integer>();
		for(int sr:srcList){			
			nodeQueue.add(sr);
			while(!nodeQueue.isEmpty()){
				int node=nodeQueue.remove();
				for(int i=0;i<adjacency.get(node).size();i++){
					int edge=adjacency.get(node).get(i);
					passed[edge]++;
					nodeQueue.add(tgts.get(edge));
				}
				adjacency.get(node).clear();
			}			
		}
		adjacency=reverseAdj;
		for(int tr:tgtList){			
			nodeQueue.add(tr);
			while(!nodeQueue.isEmpty()){
				int node=nodeQueue.remove();
				for(int i=0;i<adjacency.get(node).size();i++){
					int edge=adjacency.get(node).get(i);
					passed[edge]++;
					nodeQueue.add(srcs.get(edge));
				}
				adjacency.get(node).clear();
			}			
		}
		for(int n=0;n<nodes.size();n++) adjacency.get(n).clear();
		for(int e=0;e<edges.size();e++) if(passed[e]==2) adjacency.get(srcs.get(e)).add(e);
	}
	public void adjustAdjacency(int src,int tgt){
		int[] passed=new int[edges.size()];
		for(int i=0;i<edges.size();i++) passed[i]=0;
		ArrayList<ArrayList<Integer>> reverseAdj=new ArrayList<ArrayList<Integer>>(nodes.size());
		for(int i=0;i<nodes.size();i++) reverseAdj.add(new ArrayList<Integer>());
		for(int i=0;i<srcs.size();i++) reverseAdj.get(tgts.get(i)).add(i);
		LinkedList<Integer> nodeQueue=new LinkedList<Integer>();		
		nodeQueue.add(src);
		while(!nodeQueue.isEmpty()){
			int node=nodeQueue.remove();
			for(int i=0;i<adjacency.get(node).size();i++){
				int edge=adjacency.get(node).get(i);
				passed[edge]++;
				nodeQueue.add(tgts.get(edge));
			}
			adjacency.get(node).clear();
		}			
		adjacency=reverseAdj;		
		nodeQueue.add(tgt);
		while(!nodeQueue.isEmpty()){
			int node=nodeQueue.remove();
			for(int i=0;i<adjacency.get(node).size();i++){
				int edge=adjacency.get(node).get(i);
				passed[edge]++;
				nodeQueue.add(srcs.get(edge));
			}
			adjacency.get(node).clear();
		}			
		for(int n=0;n<nodes.size();n++) adjacency.get(n).clear();
		for(int e=0;e<edges.size();e++) if(passed[e]==2) adjacency.get(srcs.get(e)).add(e);
	}
	public void resetAdjacency(){
		adjacency=new ArrayList<ArrayList<Integer>>(nodes.size());
		for(int i=0;i<nodes.size();i++) adjacency.add(new ArrayList<Integer>());
		for(int e=0;e<edges.size();e++)	adjacency.get(srcs.get(e)).add(e);
	}
	public HashSet<Integer> extractNodes(ArrayList<Integer> srcList,ArrayList<Integer> tgtList){
		HashSet<Integer> extract=new HashSet<Integer>(tgtList);
		adjustAdjacency(srcList,tgtList);
		for(int n=0;n<nodes.size();n++) if(!adjacency.get(n).isEmpty()) extract.add(n);
		return extract;
	}
	public HashSet<Integer> extractEdges(ArrayList<Integer> srcList,ArrayList<Integer> tgtList){
		HashSet<Integer> extract=new HashSet<Integer>();
		adjustAdjacency(srcList,tgtList);
		for(int n=0;n<nodes.size();n++) extract.addAll(adjacency.get(n));
		return extract;
	}
	int getMaxDepth(){return (int)(reach*2+1);}
	double getFade(){return Math.exp(Math.log(0.05)/reach);}
	int getEdge(int src, int tgt){		
		ArrayList<Integer> edges=adjacency.get(src);
		int edge=-1;
		for(int e=0;e<edges.size();e++) if(tgts.get(edges.get(e))==tgt){edge=edges.get(e);}
		return edge;
	}
	public String getName(){return net.getRow(net).get(CyNetwork.NAME,String.class);}
	public String getName(CyNode node){return net.getRow(node).get(CyNetwork.NAME, String.class);}
	public String getName(CyEdge edge){return net.getRow(edge).get(CyNetwork.NAME, String.class);}
	public String getWeightName(CyEdge edge){return getName(edge.getSource())+"("+weights.get(edges.indexOf(edge))+")"+getName(edge.getTarget());}
}
