package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
/**
 * Influence by Active Nodes from ACTIV_IN to result attribute result in ACTIV_OUT
 * Simple multiplication of matrixes
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class InfluenceByAttribute  extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	final public static String title="Influence by Active Nodes as Attribute";
	final String activInAttr="ACTIV_IN";
	final String activOutAttr="ACTIV_OUT";
	public InfluenceByAttribute(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());	
		ModelMenuUtils menuUtils = new ModelMenuUtils(wgs,swingApplication.getJFrame());			
		if(!menuUtils.isWeighted(title)) return;
		Collection<CyColumn> columnList = applicationManager.getCurrentNetwork().getDefaultNodeTable().getColumns();
		int i;
		CyColumn col = null;
		Iterator<CyColumn> iter = columnList.iterator();
		for(i = 0;iter.hasNext();i++) {
			 col = (CyColumn) iter.next();
			if(activInAttr.equals(col.getName())) 
				break;		
		}
		if(!((i<columnList.size())&&(col.getType().isInstance(new Double(0)))&& col != null)){
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),"Floating Point Attribute "+activInAttr+" must be created",title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		menuUtils.srcDialog=new ArrayList<Integer>();
		double[] activIn=new double[wgs.nodes.size()];
		for(int s=0;s<wgs.nodes.size();s++){
			Double activ=applicationManager.getCurrentNetwork().getRow(wgs.nodes.get(s)).get(activInAttr, Double.class);
			if(activ!=null){
				activIn[s]=activ;
				menuUtils.srcDialog.add(s);
			}else activIn[s]=0.0;
		}	
		wgs.reach=menuUtils.getReach();
		if(wgs.reach==0.0) return;		
		ComputingByTreeTask cDFS=new ComputingByTreeTask(new WeightGraphTools(wgs,menuUtils.srcDialog,null,null));
		double[] activOut=cDFS.activityFromIn(activIn);
		JOptionPane.showMessageDialog(swingApplication.getJFrame(),"Results in Created Attribute "+activOutAttr+" (parameters in window title)",menuUtils.title(title),JOptionPane.INFORMATION_MESSAGE);		
		//create a new column if necessary
		if(applicationManager.getCurrentNetwork().getDefaultNodeTable().getColumn(activOutAttr) == null)
			applicationManager.getCurrentNetwork().getDefaultNodeTable().createColumn(activOutAttr, Double.class, false);
		for(int t=0;t<wgs.nodes.size();t++) 
			applicationManager.getCurrentNetwork().getRow(wgs.nodes.get(t)).set(activOutAttr, activOut[t]);
	}
}