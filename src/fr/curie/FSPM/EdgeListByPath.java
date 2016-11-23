package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
/** 
 * @author Daniel.Rovera@Curie.fr or @gmail.com
 */
public class EdgeListByPath extends ActionPathTask{
	private static final long serialVersionUID = 1L;
	final public static String title="Edge List by Path";
	public EdgeListByPath(String section){
		super(title,FSPM_App_v2.getAdapter().getCyApplicationManager(),"network",FSPM_App_v2.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(FSPM_App_v2.app+section);
	}
	public void actionPerformed(ActionEvent e){
		perform(title,ComputingThroughPath.EdgeListOBE.class,false);
	}
}