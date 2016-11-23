package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2013 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
/**
 * Empty attribute with influence computed as in influence area
 * Avoid to crash attribute of previous computing
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class ReachAreaInAttribute  extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	final public static String title="Reach Area as Attribute";
	public ReachAreaInAttribute(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e){	
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();		
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());	
		ModelMenuUtils menuUtils = new ModelMenuUtils(wgs,swingApplication.getJFrame());
		menuUtils.getSrcAllTgt(title);
		if(menuUtils.srcDialog.isEmpty()) return;
		double[] startNodes=new double[wgs.nodes.size()];
		for(int s=0;s<wgs.nodes.size();s++) if(menuUtils.srcDialog.contains(menuUtils.tgtDialog.get(s))) startNodes[s]=1.0; else startNodes[s]=0.0;
		wgs.reach=menuUtils.getReach();
		if(wgs.reach==0.0) return;	
		ComputingByTreeTask cDFS=new ComputingByTreeTask(new WeightGraphTools(wgs,menuUtils.srcDialog,null,null));
		double[] inflOnNodes=cDFS.reachAreaFromStarts(startNodes);
		String influenceAreaAttr="INFLUENCE_AREA_";		
		Collection<CyColumn> columns = applicationManager.getCurrentNetwork().getDefaultNodeTable().getColumns();
		Iterator<CyColumn> columnsIterator = columns.iterator();
		HashSet<String> attribSet=new HashSet<String>(columns.size());
		while(columnsIterator.hasNext())
			attribSet.add(((CyColumn)columnsIterator.next()).getName());
		int ni=0;while(attribSet.contains(influenceAreaAttr+ni)) ni++;
		influenceAreaAttr=influenceAreaAttr+ni;
		JOptionPane.showMessageDialog(swingApplication.getJFrame(),"Results in Created Attribute "+influenceAreaAttr+
				" (parameters in window title)\r\nThink of Keeping Parameters and Start Data",menuUtils.title(title),JOptionPane.WARNING_MESSAGE);
		
		//create a new column if necessary
		if(applicationManager.getCurrentNetwork().getDefaultNodeTable().getColumn(influenceAreaAttr) == null)
			applicationManager.getCurrentNetwork().getDefaultNodeTable().createColumn(influenceAreaAttr, Double.class, false);		
		for(int t=0;t<wgs.nodes.size();t++) 
			applicationManager.getCurrentNetwork().getRow(wgs.nodes.get(t)).set(influenceAreaAttr, inflOnNodes[t]);
	}
}
