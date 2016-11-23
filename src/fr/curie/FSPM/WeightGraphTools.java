package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Stack;
/**
 * Intermediate class between structure and iterators
 * Use depth first search to open loop by back edges, return adjacency
 * List back edges
 * Use breadth first search to find the shortest paths
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class WeightGraphTools extends WeightGraphStructure{
	int maxDepth=0;
	TextBox text;
	ArrayList<Integer> sources;
	ArrayList<Integer> targets;
	final String formatVisu="0.000";
	public WeightGraphTools(WeightGraphStructure wgs,ArrayList<Integer> sources,ArrayList<Integer> targets,TextBox text){
		super(wgs);
		this.sources=sources;
		this.targets=targets;
		this.text=text;
		maxDepth=wgs.getMaxDepth();
	}
	public WeightGraphTools(WeightGraphTools wgt){
		this.net=wgt.net;
		this.nodes=wgt.nodes;
		this.edges=wgt.edges;
		this.srcs=wgt.srcs;
		this.tgts=wgt.tgts;
		this.weights=wgt.weights;
		this.adjacency=wgt.adjacency;
		this.reach=wgt.reach;
		this.sources=wgt.sources;
		this.targets=wgt.targets;
		this.text=wgt.text;
		maxDepth=wgt.getMaxDepth();
	}
	void adjustAdjacency(){
		adjustAdjacency(sources,targets);
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
	public ArrayList<Integer> backEdgesByDFS(int root){
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
	class ShortPathByBFS{
		int root;
		Integer[][] spMx;
		ShortPathByBFS(){
			spMx=new Integer[nodes.size()][nodes.size()];
			for(int t=0;t<nodes.size();t++) for(int s=0;s<nodes.size();s++) spMx[t][s]=Integer.MAX_VALUE; 
		}
		void init(int root){
			this.root=root;
			spMx[root][root]=0;
		}
		void queueAdd(int edge){
			if(spMx[tgts.get(edge)][root]==Integer.MAX_VALUE) spMx[tgts.get(edge)][root]=spMx[srcs.get(edge)][root]+1;	
		}
	}
	void actByBFS(int root,ArrayList<ArrayList<Integer>> adjacency,ShortPathByBFS iter){
		LinkedList<Integer> nodeQueue=new LinkedList<Integer>();
		nodeQueue.add(root);
		iter.init(root);
		while(!nodeQueue.isEmpty()){
			int node=nodeQueue.remove();
			for(int i=0;i<adjacency.get(node).size();i++){
				int edge=adjacency.get(node).get(i);
				nodeQueue.add(tgts.get(edge));
				iter.queueAdd(edge);
			}
			adjacency.get(node).clear();
		}
	}
	void shortPathMatrix(){
		ShortPathByBFS sp=new ShortPathByBFS();
		for(int root:sources) actByBFS(root,copyArray(adjacency),sp);
		for(int s=0;s<sources.size();s++) {
			text.append("\t");
			text.append(getName(nodes.get(sources.get(s))));
		}
		text.append("\r\n");
		for(int t=0;t<targets.size();t++){
			text.append(getName(nodes.get(targets.get(t))));
			for(int s=0;s<sources.size();s++){
				text.append("\t");
				if(sp.spMx[targets.get(t)][sources.get(s)]==Integer.MAX_VALUE) text.append("nc");
				else text.append(sp.spMx[targets.get(t)][sources.get(s)].toString());
			}
			text.append("\r\n");
		}
	}
	void targetLine(){
		for(int s=0;s<targets.size();s++) {
			text.append("\t");
			text.append(getName(nodes.get(targets.get(s))));
		}
		text.append("\r\n");
	}
}
