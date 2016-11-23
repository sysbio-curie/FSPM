package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
/**
 * Showing if influence variation is beyond the threshold
 * This action launches the interruptible task 
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class OpeningEdgeThreshold extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	final static String title="Effect By Opening Edges with Threshold";
	public OpeningEdgeThreshold(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	class TaskFactory extends AbstractTaskFactory{
		ComputingByTreeTask ctt;
		Double fade;
		double threshold;
		ArrayList<Integer> srcDialog;
		ArrayList<Integer> tgtDialog;
		TextBox text;
		public TaskFactory(ComputingByTreeTask ctt,double threshold){
			this.ctt=ctt;
			this.threshold=threshold;
		}
		public TaskIterator createTaskIterator() {
			return(new TaskIterator(new  OpeningEdgeThresholdTask(ctt,threshold)));
		}		
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		TaskManager<?,?> taskManager=FSPM_App_v2.getAdapter().getTaskManager();		
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());
		ModelMenuUtils menuUtils = new ModelMenuUtils(wgs,swingApplication.getJFrame());
		if(!menuUtils.isWeighted(title)) return;
		wgs.reach=menuUtils.getReach();
		if(wgs.reach==0.0) return;		
		double threshold=menuUtils.getThreshold();
		if(threshold==Double.MAX_VALUE) return;
		menuUtils.getSrcTgt(title);
		if(menuUtils.srcDialog.isEmpty()|menuUtils.tgtDialog.isEmpty()) return;
		TextBox text=new TextBox(swingApplication.getJFrame(),menuUtils.title(title,threshold),0.66,1.0);
		ComputingByTreeTask ctt=new ComputingByTreeTask(new WeightGraphTools(wgs,menuUtils.srcDialog,menuUtils.tgtDialog,text));
		ctt.adjustAdjacency();
		TaskFactory taskFactory=new TaskFactory(ctt,threshold);			
		taskManager.execute(taskFactory.createTaskIterator());
		text.setVisible(true);
	}	
}
