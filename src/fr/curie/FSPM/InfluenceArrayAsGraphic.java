package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2013 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.cytoscape.model.CyNetwork;
/**
 * Display Influence Array as Paved Window using paving dialog
 * in 2 windows, according to color option
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class InfluenceArrayAsGraphic{
	String title;
	boolean blueWhiteRed;
	CyNetwork net;
	JFrame frame;
	public InfluenceArrayAsGraphic(String title,CyNetwork net,boolean blueWhiteRed,JFrame frame){
		this.title=title;
		this.net=net;
		this.blueWhiteRed=blueWhiteRed;
		this.frame=frame;
	}
	void pavingData(){
		ModelMenuUtils menuUtils = new ModelMenuUtils(net,frame);
		WeightGraphStructure wgs=new WeightGraphStructure(net);		
		if(!wgs.initWeights(net)){
			JOptionPane.showMessageDialog(frame,menuUtils.errorWeigth,title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		menuUtils.updatePathModel();
		menuUtils.updateFade();
		menuUtils.getSrcTgt(wgs,title);
		PavingData pd=new PavingData();
		pd.xNames=new String[menuUtils.srcDialog.size()];
		for(int s=0;s<menuUtils.srcDialog.size();s++) 		
			pd.xNames[s]=net.getRow(wgs.nodes.get(menuUtils.srcDialog.get(s))).get(CyNetwork.NAME, String.class);
		pd.yNames=new String[menuUtils.tgtDialog.size()];
		for(int t=0;t<menuUtils.tgtDialog.size();t++) 
			pd.yNames[t]=net.getRow(wgs.nodes.get(menuUtils.tgtDialog.get(t))).get(CyNetwork.NAME, String.class);				
		pd.values=new double[menuUtils.tgtDialog.size()][menuUtils.srcDialog.size()];
		if(menuUtils.srcDialog.isEmpty()|menuUtils.tgtDialog.isEmpty()) return;
		Double[][] inflMx;
		if(menuUtils.ifMultiPath){
			ComputingByDFS cpt=new ComputingByDFS(wgs,menuUtils.maxDepth());
			inflMx=cpt.allInfluence(menuUtils.fade, menuUtils.srcDialog);
			for(int s=0;s<menuUtils.srcDialog.size();s++){
				for(int t=0;t<menuUtils.tgtDialog.size();t++){
					pd.values[t][s]=inflMx[menuUtils.tgtDialog.get(t)][menuUtils.srcDialog.get(s)];
				}						
			}	
			PavingDialog pw=new PavingDialog(frame,menuUtils.addTitle(title),"Source\tTarget\tInfluence",pd,false,blueWhiteRed);
			pw.setVisible(true);
		}else{
			ComputingByBFS cpt=new ComputingByBFS(wgs);
			inflMx=cpt.allInfluence(menuUtils.fade, menuUtils.srcDialog);
			for(int s=0;s<menuUtils.srcDialog.size();s++){
				for(int t=0;t<menuUtils.tgtDialog.size();t++){
					pd.values[t][s]=inflMx[menuUtils.tgtDialog.get(t)][menuUtils.srcDialog.get(s)];
				}						
			}	
			PavingDialog pw=new PavingDialog(frame,menuUtils.addTitle(title),"Source\tTarget\tInfluence",pd,false,blueWhiteRed);
			pw.setVisible(true);
		}
		return;
	}
}
