package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
 */
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Classes made to be iterated through all paths
 * List of nodes and edges by path
 * Signed distance matrix and influence as list
 * Iterate through a tree when OBE (Open Back Edge)
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class ComputingThroughPath extends WeightGraphTools{
	public ComputingThroughPath(WeightGraphTools wgt){
		super(wgt);
	}
	abstract class IterThroughPath{
		void begin(int root){}
		void doByPath(LinkedList<Integer> chain){}
		void end(){}
	}
	class NodeListAll extends IterThroughPath{
		void doByPath(LinkedList<Integer> chain){
			for(int n:chain){
				text.append(getName(nodes.get(n)));
				text.append("\t");
			}
			text.append("\r\n");	
		}
	}
	class NodeListOBE extends NodeListAll{}
	class EdgeListAll extends IterThroughPath{
		void doByPath(LinkedList<Integer> chain){
			for(int n=0;n<chain.size()-1;n++){
				text.append(getName(edges.get(getEdge(chain.get(n),chain.get(n+1)))));
				text.append("\t");
			}
			text.append("\r\n");	
		}
	}
	class EdgeListOBE extends EdgeListAll{}
	class WeigthEdgeListAll extends IterThroughPath{
		void doByPath(LinkedList<Integer> chain){
			for(int n=0;n<chain.size()-1;n++){
				text.append(getWeightName(edges.get(getEdge(chain.get(n),chain.get(n+1)))));
				text.append("\t");
			}
			text.append("\r\n");	
		}
	}
	class WeigthEdgeListOBE extends WeigthEdgeListAll{}
	class SignedDistanceAll extends IterThroughPath{		
		int root;
		ArrayList<Integer>[] distance;
		void begin(int root){
			this.root=root;
			distance=new ArrayList[nodes.size()];
			for(int t=0;t<nodes.size();t++) distance[t]=(new ArrayList<Integer>());
		}
		void doByPath(LinkedList<Integer> chain){			
			int tgt=chain.get(chain.size()-1);
			double sgn=1;int length=0;
			for(int n=0;n<chain.size()-1;n++){
				sgn=sgn*weights.get(getEdge(chain.get(n),chain.get(n+1)));
				length++;
			}
			distance[tgt].add((int)(length*Math.signum(sgn)));			
		}
		void end(){
			for(int t:targets){
				text.append(getName(nodes.get(root)));
				text.append("\t");
				text.append(getName(nodes.get(t)));
				for(int p:distance[t]){
					text.append("\t");
					text.append(Integer.toString(p));				
				}
				text.append("\r\n");
			}
		}
	}
	class InfluenceListAll extends IterThroughPath{		
		int root;
		double fade;
		Double[] influence;
		void begin(int root){
			this.root=root;
			influence=new Double[nodes.size()];
			for(int n=0;n<nodes.size();n++) influence[n]=0.0;
			influence[root]=1.0;
			fade=getFade();
		}
		void doByPath(LinkedList<Integer> chain){
			int tgt=chain.get(chain.size()-1);
			double rootInfl=1.0;
			for(int n=0;n<chain.size()-1;n++) rootInfl=fade*weights.get(getEdge(chain.get(n),chain.get(n+1)))*rootInfl;
			influence[tgt]=influence[tgt]+rootInfl;	
		}
		void end(){			
			for(int t:targets){
				text.append(getName(nodes.get(root)));
				text.append("\t");
				text.append(getName(nodes.get(t)));
				text.append("\t");
				if(influence[t].isNaN()) text.append("0.0");else text.append(influence[t].toString());
				text.append("\r\n");
			}			
		}
	}
	class InfluenceArrayAll extends InfluenceListAll{
		void end(){
			text.append(getName(nodes.get(root)));
			for(int t:targets){
				text.append("\t");
				if(influence[t].isNaN()) text.append("0.0");else text.append(influence[t].toString());
			}
			text.append("\r\n");
		}
	}
}
