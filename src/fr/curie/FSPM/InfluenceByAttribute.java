package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2010-2013 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
/**
 * Influence by Active Nodes from ACTIV_IN to result attribute result in ACTIV_OUT
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class InfluenceByAttribute  extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="Influence by Active Nodes as Attribute";
	final String activInAttr="ACTIV_IN";
	final String activOutAttr="ACTIV_OUT";
	public InfluenceByAttribute(CySwingAppAdapter adapter){
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
			//Cytoscape.getNodeAttributes().getDoubleAttribute(wgs.nodes.get(s).getIdentifier(),activInAttr);
			if(activ!=null){
				activIn[s]=activ;
				menuUtils.srcDialog.add(s);
			}else activIn[s]=0.0;
		}	
		menuUtils.updatePathModel();
		menuUtils.updateFade();
		double[] activOut;
		if(menuUtils.ifMultiPath){
			ComputingByDFS cpt=new ComputingByDFS(wgs,menuUtils.maxDepth());
			activOut=cpt.activityFromIn(menuUtils.fade,menuUtils.srcDialog,activIn);
		}else{
			ComputingByBFS cpt=new ComputingByBFS(wgs);
			activOut=cpt.activityFromIn(menuUtils.fade,menuUtils.srcDialog,activIn);
		}
		JOptionPane.showMessageDialog(swingApplication.getJFrame(),"Results in Created Attribute "+activOutAttr+" (parameters in window title)",menuUtils.addTitle(title),JOptionPane.INFORMATION_MESSAGE);
		
		//create a new column if necessary
		if(applicationManager.getCurrentNetwork().getDefaultNodeTable().getColumn(activOutAttr) == null)
			applicationManager.getCurrentNetwork().getDefaultNodeTable().createColumn(activOutAttr, Double.class, false);

		for(int t=0;t<wgs.nodes.size();t++) 
			applicationManager.getCurrentNetwork().getRow(wgs.nodes.get(t)).set(activOutAttr, activOut[t]);
			//Cytoscape.getNodeAttributes().setAttribute(wgs.nodes.get(t).getIdentifier(),activOutAttr,activOut[t]);
	}
}