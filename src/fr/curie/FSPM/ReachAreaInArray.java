package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2013 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;

/**
 * Display Influence Area from start nodes
 * in text box
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class ReachAreaInArray  extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="Display Influence Reach Area in Array";
	public ReachAreaInArray(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap5);
		this.adapter = adapter;
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		ModelMenuUtils menuUtils = new ModelMenuUtils(applicationManager.getCurrentNetwork(),swingApplication.getJFrame());
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());		
		menuUtils.updatePathModel();
		menuUtils.updateFade();
		menuUtils.getSrcAllTgt(wgs,title,applicationManager.getCurrentNetwork());
		if(menuUtils.srcDialog.isEmpty()) return;;
		Double[][] areaMx;
		if(menuUtils.ifMultiPath){
			ComputingByDFS cpt=new ComputingByDFS(wgs,menuUtils.maxDepth());
			areaMx=cpt.reachArea(menuUtils.fade, menuUtils.srcDialog);
			new TextBox(swingApplication.getJFrame(), menuUtils.addTitle(title),menuUtils.matrixToFormatTxt(cpt,areaMx,null,applicationManager.getCurrentNetwork()).toString()).setVisible(true);
		}else{
			ComputingByBFS cpt=new ComputingByBFS(wgs);
			areaMx=cpt.reachArea(menuUtils.fade, menuUtils.srcDialog);
			new TextBox(swingApplication.getJFrame(), menuUtils.addTitle(title),menuUtils.matrixToFormatTxt(cpt,areaMx,null,applicationManager.getCurrentNetwork())).setVisible(true);
		}
	}
}
