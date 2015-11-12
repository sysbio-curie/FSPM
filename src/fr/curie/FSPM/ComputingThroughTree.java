package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2013 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import org.cytoscape.model.CyNetwork;
/**
 * Classes made to be iterated through BFS trees and so
 * Select edges
 * Shortest path matrix
 * Signed distance matrix
 * Compute influence, activity and reach area
 * Use one of both path model
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class ComputingThroughTree extends WeightGraphStructure{
	public ComputingThroughTree(){super();}
	public ComputingThroughTree(CyNetwork gp){super(gp);}
	abstract class IterThroughTree{
		int root;
		IterThroughTree(){}
		void init(int root){this.root=root;}
		void queueRemove(int node){}
		void queueAdd(int edge){}
	}
	class EdgesByBFS extends IterThroughTree{
		HashSet<Integer> edges;
		EdgesByBFS(int setSize){
			edges=new HashSet<Integer>(setSize);
		}
		void queueAdd(int edge){
			edges.add(edge);
		}
	}
	class ShortPathByBFS extends IterThroughTree{
		int root;
		int[][] spMx;
		ShortPathByBFS(){
			spMx=new int[nodes.size()][nodes.size()];
			for(int i=0;i<nodes.size();i++) for(int j=0;j<nodes.size();j++) spMx[i][j]=Integer.MAX_VALUE; 
		}
		void init(int root){
			this.root=root;
			spMx[root][root]=0;
		}
		void queueAdd(int edge){
			if(spMx[tgts.get(edge)][root]==Integer.MAX_VALUE) spMx[tgts.get(edge)][root]=spMx[srcs.get(edge)][root]+1;	
		}
	}
	class SignedDistanceByITT extends IterThroughTree{
		int depth;
		LinkedList<Integer> levelQueue;
		ArrayList<Integer>[][] distance;
		public SignedDistanceByITT(){
			levelQueue=new LinkedList<Integer>();
			distance=new ArrayList[nodes.size()][nodes.size()];
			for(int i=0;i<nodes.size();i++)for(int j=0;j<nodes.size();j++) distance[i][j]=(new ArrayList<Integer>());
		}
		void init(int root){
			this.root=root;
			depth=0;
			levelQueue.clear();
			levelQueue.add(depth);
		}
		void queueRemove(int node){
			depth=levelQueue.remove();
			if(depth<0) depth--; else depth++;
		}
		protected void queueAdd(int edge){			
			if(weights.get(edge)<0){
				distance[tgts.get(edge)][root].add(-depth);
				levelQueue.add(-depth);
			}else{
				distance[tgts.get(edge)][root].add(depth);
				levelQueue.add(depth);
			}
		}
	}
	class InfluenceByITT extends IterThroughTree{
		double fade;
		double value;
		LinkedList<Double> valueQueue;
		Double[][] influence;
		public InfluenceByITT(double fade){
			this.fade=fade;
			valueQueue=new LinkedList<Double>();
			influence=new Double[nodes.size()][nodes.size()];
			for(int i=0;i<nodes.size();i++)for(int j=0;j<nodes.size();j++) influence[i][j]=Double.NaN;
		}
		void init(int root){
			this.root=root;
			influence[root][root]=1.0;
			valueQueue.clear();
			valueQueue.add(1.0);
		}
		void queueRemove(int node){
			value=valueQueue.remove();
		}
		protected void queueAdd(int edge){
			double newValue=fade*weights.get(edge)*value;
			valueQueue.add(newValue);
			if(influence[tgts.get(edge)][root].isNaN()) influence[tgts.get(edge)][root]=newValue;
			else influence[tgts.get(edge)][root]=influence[tgts.get(edge)][root]+newValue;
		}
	}
	class ActivityByITT extends IterThroughTree{
		double fade;
		double value;
		LinkedList<Double> valueQueue;
		double[] activIn;
		Double[][] activity;
		public ActivityByITT(double fade,double[] activIn){
			this.fade=fade;
			this.activIn=activIn;
			valueQueue=new LinkedList<Double>();
			activity=new Double[nodes.size()][nodes.size()];
			for(int i=0;i<nodes.size();i++)for(int j=0;j<nodes.size();j++) activity[i][j]=0.0;
		}
		void init(int root){
			this.root=root;
			activity[root][root]=activIn[root];
			valueQueue.clear();
			valueQueue.add(activIn[root]);
		}
		void queueRemove(int node){
			value=valueQueue.remove();
		}
		protected void queueAdd(int edge){
			double newValue=fade*weights.get(edge)*value;
			valueQueue.add(newValue);
			activity[tgts.get(edge)][root]=activity[tgts.get(edge)][root]+newValue;
		}
	}
	class ReachAreaByITT extends IterThroughTree{
		double fade;
		double value;
		LinkedList<Double> valueQueue;
		Double[][] area;
		public ReachAreaByITT(double fade){
			this.fade=fade;
			valueQueue=new LinkedList<Double>();
			area=new Double[nodes.size()][nodes.size()];
			for(int i=0;i<nodes.size();i++)for(int j=0;j<nodes.size();j++) area[i][j]=0.0;
		}
		void init(int root){
			this.root=root;
			area[root][root]=1.0;
			valueQueue.clear();
			valueQueue.add(1.0);
		}
		void queueRemove(int node){
			value=valueQueue.remove();
		}
		protected void queueAdd(int edge){
			double newValue=fade*value;
			valueQueue.add(newValue);
			area[tgts.get(edge)][root]=area[tgts.get(edge)][root]+newValue;
		}
	}
}
