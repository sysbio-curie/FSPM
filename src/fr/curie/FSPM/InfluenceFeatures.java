package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
/**
 * Display the Network  size and Parameter Features as 
 * connectivity, rate of connected nodes in influence matrix
 * mean, minimum and maximum influence
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class InfluenceFeatures extends AbstractCyAction{	
	private static final long serialVersionUID = 1L;
	final public static String title="Network and Parameter Features";
	CyApplicationManager applicationManager;
	CySwingApplication swingApplication;
	ModelMenuUtils menuUtils;
	public InfluenceFeatures(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	private void features(WeightGraphStructure wgs,Double[][] infl){
		int nc=0;
		double mean=0.0;
		double sqrSum=0;
		double min=Double.MAX_VALUE;
		double max=Double.MIN_VALUE;
		for(int t=0;t<wgs.nodes.size();t++)	for(int s=0;s<wgs.nodes.size();s++){
			if((infl[t][s]).isNaN()) nc++;
			else{
				mean=mean+infl[t][s];
				sqrSum=sqrSum+infl[t][s]*infl[t][s];
				if(infl[t][s]<min) min=infl[t][s]; else if(infl[t][s]>max) max=infl[t][s];
			}
		}		
		double connect=(double)(wgs.nodes.size()*wgs.nodes.size()-nc)/(double)(wgs.nodes.size()*wgs.nodes.size());
		mean=mean/(wgs.nodes.size()*wgs.nodes.size()-nc);
		sqrSum=sqrSum/(wgs.nodes.size()*wgs.nodes.size()-nc);
		double sigma=Math.sqrt(sqrSum-mean*mean);
		String txt="Network\t"+applicationManager.getCurrentNetwork().getRow(applicationManager.getCurrentNetwork()).get(CyNetwork.NAME, String.class);;
		txt=txt+"\r\nReach\t"+wgs.reach+"\r\nFade\t"+wgs.getFade()+"\r\nConnectivity\t"+connect;
		txt=txt+"\r\nMeanInfluence\t"+mean+"\r\nStandardDeviation\t"+sigma+"\r\nMinInfluence\t"+min+"\r\nMaxInfluence\t"+max;
		(new TextBox(swingApplication.getJFrame(),title,txt,0.5,0.3)).setVisible(true);	
	}
	public void actionPerformed(ActionEvent e){
		applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());		
		menuUtils=new ModelMenuUtils(wgs,swingApplication.getJFrame());
		if(!wgs.initWeights()){
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),menuUtils.errorWeigth,title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		wgs.reach=menuUtils.getReach();
		if(wgs.reach==0.0) return;	
		menuUtils.getAllSrcAllTgt();
		ComputingByTreeTask cpt=new ComputingByTreeTask(new WeightGraphTools(wgs,menuUtils.srcDialog,null,null));
		features(cpt,cpt.allInfluence());	
	}
}
