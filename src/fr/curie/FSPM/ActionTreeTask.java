package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import fr.curie.FSPM.ComputingThroughTree.IterThroughTree;
/**
 * Task Factory for Computing by tree after opening loops
 * Common method to all functions computing by tree
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class ActionTreeTask  extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	public ActionTreeTask(String name, CyApplicationManager cyApplicationManager, String string, CyNetworkViewManager cyNetworkViewManager) {
		super(name,cyApplicationManager,string,cyNetworkViewManager);
	}
	protected class TaskFactory extends AbstractTaskFactory{
		WeightGraphTools wgt;
		TextBox text;
		Class<? extends IterThroughTree> iterClass;
		public TaskFactory(WeightGraphTools wgt,Class<? extends IterThroughTree> iterClass){
			this.wgt=wgt;
			this.iterClass=iterClass;
		}
		public TaskIterator createTaskIterator() {
			return(new TaskIterator(new ComputingByTreeTask(wgt,iterClass)));
		}		
	}
	void perform(String title,Class<? extends IterThroughTree> iterClass){
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		TaskManager<?,?> taskManager=FSPM_App_v2.getAdapter().getTaskManager();
		WeightGraphStructure wgs=new WeightGraphStructure(applicationManager.getCurrentNetwork());
		ModelMenuUtils menuUtils = new ModelMenuUtils(wgs,swingApplication.getJFrame());
		if(!menuUtils.isWeighted(title)) return;
		wgs.reach=menuUtils.getReach();
		if(wgs.reach==0.0) return;
		menuUtils.getSrcTgt(title);
		if(menuUtils.srcDialog.isEmpty()|menuUtils.tgtDialog.isEmpty()) return;
		TextBox text=new TextBox(swingApplication.getJFrame(),menuUtils.title(title),0.66,1.0);
		WeightGraphTools wgt=new WeightGraphTools(wgs,menuUtils.srcDialog,menuUtils.tgtDialog,text);
		wgt.adjustAdjacency();
		TaskFactory taskFactory=new TaskFactory(wgt,iterClass);			
		taskManager.execute(taskFactory.createTaskIterator());
		text.setVisible(true);
	}
	public void actionPerformed(ActionEvent e){}
}
