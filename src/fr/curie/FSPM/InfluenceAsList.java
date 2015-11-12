package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2013 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
/**
 * Display Influence Array in a list
 * for computing, non connected=0, all possible digits * 
 * @author Daniel.Rovera@curie.fr
 */
public class InfluenceAsList  extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="Display Influence As List";
	public InfluenceAsList(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap3);
		this.adapter = adapter;
	}
	public void actionPerformed(ActionEvent e){		
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		ModelMenuUtils menuUtils = new ModelMenuUtils(applicationManager.getCurrentNetwork(),swingApplication.getJFrame());
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());		
		if(!wgs.initWeights(applicationManager.getCurrentNetwork())){
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),menuUtils.errorWeigth,title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		menuUtils.updatePathModel();
		menuUtils.updateFade();
		menuUtils.getSrcTgt(wgs,title);
		if(menuUtils.srcDialog.isEmpty()|menuUtils.tgtDialog.isEmpty()) return;
		Double[][] inflMx;
		if(menuUtils.ifMultiPath){
			ComputingByDFS cpt=new ComputingByDFS(wgs,menuUtils.maxDepth());
			inflMx=cpt.allInfluence(menuUtils.fade, menuUtils.srcDialog);
			new TextBox(swingApplication.getJFrame(),menuUtils.addTitle(title),menuUtils.matrixToList(cpt,inflMx,applicationManager.getCurrentNetwork())).setVisible(true);
		}else{
			ComputingByBFS cpt=new ComputingByBFS(wgs);
			inflMx=cpt.allInfluence(menuUtils.fade, menuUtils.srcDialog);
			new TextBox(swingApplication.getJFrame(),menuUtils.addTitle(title),menuUtils.matrixToList(cpt,inflMx,applicationManager.getCurrentNetwork())).setVisible(true);
		}
	}
}
