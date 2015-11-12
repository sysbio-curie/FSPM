package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
/**
 * Display the Network  size and Parameter Features as 
 * connectivity, rate of connected nodes in influence matrix
 * mean, minimum and maximum influence
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class InfluenceFeatures extends AbstractCyAction{	
	private CySwingAppAdapter adapter;
	private static final long serialVersionUID = 1L;
	final public static String title="Display Network and Parameter Features";
	CyApplicationManager applicationManager;
	CySwingApplication swingApplication;
	ModelMenuUtils menuUtils;
	public InfluenceFeatures(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap2);
		this.adapter = adapter;
	}

	private void features(WeightGraphStructure cpt,Double[][] infl,String model){
		int nc=0;
		double mean=0.0;
		double sqrSum=0;
		double min=Double.MAX_VALUE;
		double max=Double.MIN_VALUE;
		for(int t=0;t<cpt.nodes.size();t++)	for(int s=0;s<cpt.nodes.size();s++){
			if((infl[t][s]).isNaN()) nc++;
			else{
				mean=mean+infl[t][s];
				sqrSum=sqrSum+infl[t][s]*infl[t][s];
				if(infl[t][s]<min) min=infl[t][s]; else if(infl[t][s]>max) max=infl[t][s];
			}
		}		
		double connect=(double)(cpt.nodes.size()*cpt.nodes.size()-nc)/(double)(cpt.nodes.size()*cpt.nodes.size());
		mean=mean/(cpt.nodes.size()*cpt.nodes.size()-nc);
		sqrSum=sqrSum/(cpt.nodes.size()*cpt.nodes.size()-nc);
		double sigma=Math.sqrt(sqrSum-mean*mean);
		String txt="Network\t"+applicationManager.getCurrentNetwork().getRow(applicationManager.getCurrentNetwork()).get(CyNetwork.NAME, String.class);;
		txt=txt+"\r\nReach\t"+menuUtils.reach+"\r\nFade\t"+menuUtils.fade+"\r\nModel\t"+model+"\r\nConnectivity\t"+connect;
		txt=txt+"\r\nMeanInfluence\t"+mean+"\r\nStandardDeviation\t"+sigma+"\r\nMinInfluence\t"+min+"\r\nMaxInfluence\t"+max;
		new TextBox(swingApplication.getJFrame(),title,txt).setVisible(true);	
	}
	public void actionPerformed(ActionEvent e){
		applicationManager=adapter.getCyApplicationManager();
		swingApplication=adapter.getCySwingApplication();
		menuUtils = new ModelMenuUtils(applicationManager.getCurrentNetwork(),swingApplication.getJFrame());
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());		
		if(!wgs.initWeights(applicationManager.getCurrentNetwork())){
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),menuUtils.errorWeigth,title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		menuUtils.updatePathModel();
		menuUtils.updateFade();
		menuUtils.srcDialog=new ArrayList<Integer>();
		for(int n=0;n<wgs.nodes.size();n++) menuUtils.srcDialog.add(n);
		if(menuUtils.ifMultiPath){
			ComputingByDFS cpt=new ComputingByDFS(wgs,menuUtils.maxDepth());
			features(cpt,cpt.allInfluence(menuUtils.fade,menuUtils.srcDialog),"MultiPath");
		}else{
			ComputingByBFS cpt=new ComputingByBFS(wgs);
			features(cpt,cpt.allInfluence(menuUtils.fade,menuUtils.srcDialog),"MonoPath");
		}		
	}
}
