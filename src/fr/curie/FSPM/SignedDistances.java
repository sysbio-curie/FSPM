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
	 * Display matrix of signed distance between nodes using BFS route
	 * Distance is counted by number of edges and signed by signs of paths based on weights
	 * 
	 * @author Daniel.Rovera@curie.fr
	 */
public class SignedDistances extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final public static String title="Display Signed Distances";
	private CySwingAppAdapter adapter;
	public SignedDistances(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());		
		setPreferredMenu(Ttls.app+Ttls.chap1);
		insertSeparatorAfter();
		this.adapter = adapter;
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		ModelMenuUtils menuUtils = new ModelMenuUtils(applicationManager.getCurrentNetwork(),swingApplication.getJFrame());
		CyNetwork net=applicationManager.getCurrentNetwork();
		WeightGraphStructure wgs=new WeightGraphStructure(net);
		if(!wgs.initWeights(applicationManager.getCurrentNetwork())){
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),menuUtils.errorWeigth,title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		menuUtils.updatePathModel();		
		menuUtils.getSrcTgt(wgs,title);
		ArrayList<Integer>[][] sdMx;
		if(menuUtils.ifMultiPath){
			menuUtils.updateFade();
			ComputingByDFS cpt=new ComputingByDFS(wgs,menuUtils.maxDepth());
			sdMx=cpt.signedDistances(menuUtils.srcDialog);
			new TextBox(swingApplication.getJFrame(),menuUtils.addTitle(title),menuUtils.matrixToTxt(cpt,sdMx, net)).setVisible(true);
		}else{
			ComputingByBFS cpt=new ComputingByBFS(wgs);
			sdMx=cpt.signedDistances(menuUtils.srcDialog);
			new TextBox(swingApplication.getJFrame(),menuUtils.addTitle(title),menuUtils.matrixToTxt(cpt,sdMx, net)).setVisible(true);
		}
	}
}
