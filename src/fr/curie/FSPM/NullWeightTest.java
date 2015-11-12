package fr.curie.FSPM;

/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.work.swing.DialogTaskManager;
/**
 * Display list of edges improving score by being cut, weight=0
 * sorted by decreasing kappa 
 * 
 * @author Daniel.Rovera@curie.fr
 *
 */
public class NullWeightTest extends AbstractCyAction {	
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="Test Score by Canceling Weight";
	public NullWeightTest(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap6);
		this.adapter = adapter;
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		DialogTaskManager dialogTaskManager=adapter.getDialogTaskManager();
		ComputeKappaNullWeight ck=new ComputeKappaNullWeight(applicationManager,swingApplication);		
		if(ck.getSetData()){
			dialogTaskManager.execute(ck.new KappaTaskFactory(ck,title).createTaskIterator());						
		}else JOptionPane.showMessageDialog(ck.frame,ck.aimError,title,JOptionPane.ERROR_MESSAGE);
	}
}