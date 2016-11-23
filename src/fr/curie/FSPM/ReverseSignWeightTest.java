package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.work.TaskManager;
/**
 * Display list of edges improving score by reversing weight, weight=-weight
 * sorted by decreasing kappa if not interrupted
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class ReverseSignWeightTest extends AbstractCyAction  {
	private static final long serialVersionUID = 1L;
	final public static String title="Test Score by Reversing Sign Weight";
	public ReverseSignWeightTest(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=FSPM_App_v2.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=FSPM_App_v2.getAdapter().getCySwingApplication();
		TaskManager<?, ?> taskManager=FSPM_App_v2.getAdapter().getTaskManager();
		ComputeKappaReverseSign ck=new ComputeKappaReverseSign(applicationManager.getCurrentNetwork(),swingApplication.getJFrame());
		if(ck.getSetData())	taskManager.execute(ck.new KappaTaskFactory(ck,title).createTaskIterator());						
		else{
			ck.txt.setVisible(false);
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),ck.aimError,title,JOptionPane.ERROR_MESSAGE);;	
		}
	}
}