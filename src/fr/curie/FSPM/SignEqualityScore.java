package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;

/**
 * Display data and results numbers and kappa
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class SignEqualityScore extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	private CySwingAppAdapter adapter;
	final public static String title="Compute Score of Data Sets";	
	public SignEqualityScore(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap6);
		this.adapter = adapter;
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		ComputeKappa ck=new ComputeKappa(applicationManager,swingApplication);
		StringBuffer txt;
		if(ck.getSetData()) txt=ck.displayScore(); else txt=new StringBuffer(ck.aimError);
		new TextBox(swingApplication.getJFrame(),title,txt.toString()).setVisible(true);	
	}
}
