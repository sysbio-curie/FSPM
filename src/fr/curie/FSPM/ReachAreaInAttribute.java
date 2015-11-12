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
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
/**
 * Empty attribute with influence computed as in influence area
 * Avoid to crash attribute of previous computing
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class ReachAreaInAttribute  extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="Influence Reach Area as Attribute";
	public ReachAreaInAttribute(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap5);
		this.adapter = adapter;
	}
	public void actionPerformed(ActionEvent e){	
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		ModelMenuUtils menuUtils = new ModelMenuUtils(applicationManager.getCurrentNetwork(),swingApplication.getJFrame());
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());		
		menuUtils.getSrcAllTgt(wgs,title,applicationManager.getCurrentNetwork());
		if(menuUtils.srcDialog.isEmpty()) return;
		double[] startNodes=new double[wgs.nodes.size()];
		for(int s=0;s<wgs.nodes.size();s++) if(menuUtils.srcDialog.contains(menuUtils.tgtDialog.get(s))) startNodes[s]=1.0; else startNodes[s]=0.0;
		menuUtils.updatePathModel();
		menuUtils.updateFade();
		double[] inflOnNodes;
		if(menuUtils.ifMultiPath){
			ComputingByDFS cpt=new ComputingByDFS(wgs,menuUtils.maxDepth());
			inflOnNodes=cpt.reachAreaFromStarts(menuUtils.fade,menuUtils.srcDialog,startNodes);
		}else{
			ComputingByBFS cpt=new ComputingByBFS(wgs);
			inflOnNodes=cpt.reachAreaFromStarts(menuUtils.fade,menuUtils.srcDialog,startNodes);
		}
		String influenceAreaAttr="INFLUENCE_AREA_";
		
		//String[] attrNames=Cytoscape.getNodeAttributes().getAttributeNames();
		Collection<CyColumn> columns = applicationManager.getCurrentNetwork().getDefaultNodeTable().getColumns();
		Iterator<CyColumn> columnsIterator = columns.iterator();
		HashSet<String> attribSet=new HashSet<String>(columns.size());
		while(columnsIterator.hasNext())
			attribSet.add(((CyColumn)columnsIterator.next()).getName());
		//for(int i=0;i<attrNames.length;i++) 
			
		
		int ni=0;while(attribSet.contains(influenceAreaAttr+ni)) ni++;
		influenceAreaAttr=influenceAreaAttr+ni;
		JOptionPane.showMessageDialog(swingApplication.getJFrame(),"Results in Created Attribute "+influenceAreaAttr+
				" (parameters in window title)\r\nThink of Keeping Parameters and Start Data",menuUtils.addTitle(title),JOptionPane.WARNING_MESSAGE);
		
		//create a new column if necessary
		if(applicationManager.getCurrentNetwork().getDefaultNodeTable().getColumn(influenceAreaAttr) == null)
			applicationManager.getCurrentNetwork().getDefaultNodeTable().createColumn(influenceAreaAttr, Double.class, false);
		
		for(int t=0;t<wgs.nodes.size();t++) 
			applicationManager.getCurrentNetwork().getRow(wgs.nodes.get(t)).set(influenceAreaAttr, inflOnNodes[t]);
			//Cytoscape.getNodeAttributes().setAttribute(wgs.nodes.get(t).getIdentifier(),influenceAreaAttr,inflOnNodes[t]);
	}
}
