package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
/**
 * Classes made to be iterated through trees, loops having been opened
 * Select edges between nodes
 * Shortest path matrix,
 * Signed distance matrix, influence, activity and reach area
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class ComputingThroughTree extends WeightGraphTools{
	double fade;
	public ComputingThroughTree(WeightGraphTools wgt){
		super(wgt);
		fade=wgt.getFade();
	}
	abstract class IterThroughTree{
		int root;
		IterThroughTree(){}
		void init(int root){this.root=root;}
		void queueRemove(int node){}
		void queueAdd(int edge){}
		void end(){};
	}
	class SignedDistance extends IterThroughTree{
		int depth;
		LinkedList<Integer> levelQueue;
		ArrayList<Integer>[][] distance;
		public SignedDistance(){
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
		void queueAdd(int edge){			
			if(weights.get(edge)<0){
				distance[tgts.get(edge)][root].add(-depth);
				levelQueue.add(-depth);
			}else{
				distance[tgts.get(edge)][root].add(depth);
				levelQueue.add(depth);
			}
		}
		void end(){
			text.append(getName(nodes.get(root)));
			for(int t:targets){
				text.append("\t");
				text.append(distance[t][root].toString());
			}
			text.append("\r\n");
		}	
	}
	class SignedDistancList extends SignedDistance{
		public SignedDistancList(){super();}
		void end(){
			for(int t:targets){
				text.append(getName(nodes.get(root)));
				text.append("\t");
				text.append(getName(nodes.get(t)));
				for(int p:distance[t][root]){
					text.append("\t");
					text.append(Integer.toString(p));
				}
				text.append("\r\n");
			}
		}
	}
	class InfluenceOnly extends IterThroughTree{
		double value;
		LinkedList<Double> valueQueue;
		Double[][] influence;
		public InfluenceOnly(){
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
		void queueAdd(int edge){
			double newValue=fade*weights.get(edge)*value;
			valueQueue.add(newValue);
			if(influence[tgts.get(edge)][root].isNaN()) influence[tgts.get(edge)][root]=newValue;
			else influence[tgts.get(edge)][root]=influence[tgts.get(edge)][root]+newValue;
		}
	}
	class InfluenceComp extends InfluenceOnly{
		public InfluenceComp(){super();}
		void end(){
			text.append(getName(nodes.get(root)));
			for(int t:targets){
				text.append("\t");
				if(influence[t][root].isNaN()) text.append("0.0");else text.append(influence[t][root].toString());
			}
			text.append("\r\n");
		}
	}
	class InfluenceVisu extends InfluenceOnly{
		public InfluenceVisu(){super();}
		void end(){
			text.append(getName(nodes.get(root)));
			for(int t:targets){
				text.append("\t");
				if(influence[t][root].isNaN()) text.append("nc");else text.append((new DecimalFormat(formatVisu)).format(influence[t][root]));
			}
			text.append("\r\n");
		}
	}
	class InfluenceList extends InfluenceOnly{
		public InfluenceList(){super();}
		void end(){
			for(int t:targets){
				text.append(getName(nodes.get(root)));
				text.append("\t");
				text.append(getName(nodes.get(t)));
				text.append("\t");
				if(influence[t][root].isNaN()) text.append("0.0");else text.append(influence[t][root].toString());
				text.append("\r\n");
			}
		}
	}
	class ActivityByITT extends IterThroughTree{
		double value;
		LinkedList<Double> valueQueue;
		double[] activIn;
		Double[][] activity;
		public ActivityByITT(double[] activIn){
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
		void queueAdd(int edge){
			double newValue=fade*weights.get(edge)*value;
			valueQueue.add(newValue);
			activity[tgts.get(edge)][root]=activity[tgts.get(edge)][root]+newValue;
		}
	}
	class ReachAreaOnly extends IterThroughTree{
		double value;
		LinkedList<Double> valueQueue;
		Double[][] area;
		public ReachAreaOnly(){
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
		void queueAdd(int edge){
			double newValue=fade*value;
			valueQueue.add(newValue);
			area[tgts.get(edge)][root]=area[tgts.get(edge)][root]+newValue;
		}
	}
	class ReachAreaText extends ReachAreaOnly{
		public ReachAreaText(){super();}
		void end(){
			text.append(getName(nodes.get(root)));
			for(int t:targets){
				text.append("\t");
				if(area[t][root].isNaN()) text.append("0.0");else text.append(area[t][root].toString());
			}
			text.append("\r\n");
		}
	}
}
