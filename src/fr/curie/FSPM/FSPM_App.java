package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE  
*/
import org.cytoscape.app.swing.AbstractCySwingApp;
import org.cytoscape.app.swing.CySwingAppAdapter;
/**
 * Cytoscape application launching actions
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class  FSPM_App extends AbstractCySwingApp{
	public FSPM_App(CySwingAppAdapter adapter){		
		super(adapter);

		adapter.getCySwingApplication().addAction(new PreselectSrcTgt(adapter));
		adapter.getCySwingApplication().addAction(new FromToNodes(adapter));
		adapter.getCySwingApplication().addAction(new SCCinAttribute(adapter));
		adapter.getCySwingApplication().addAction(new SignedDistances(adapter));
		adapter.getCySwingApplication().addAction(new OpenedBackEdges(adapter));
		
		adapter.getCySwingApplication().addAction(new UpdateInfluenceAttrib(adapter));
		adapter.getCySwingApplication().addAction(new ChooseModelType(adapter));				
		adapter.getCySwingApplication().addAction(new InputReachParameter(adapter));
		adapter.getCySwingApplication().addAction(new InputScoreThreshold(adapter));		
		adapter.getCySwingApplication().addAction(new InfluenceFeatures(adapter));

		adapter.getCySwingApplication().addAction(new InfluenceArrayForVisu(adapter));
		adapter.getCySwingApplication().addAction(new InfluenceArrayForComputing(adapter));
		adapter.getCySwingApplication().addAction(new InfluenceAsList(adapter));				
		adapter.getCySwingApplication().addAction(new InfluenceByAttribute(adapter));
		adapter.getCySwingApplication().addAction(new InfluenceBetweenSubnetworks(adapter));
		
		adapter.getCySwingApplication().addAction(new InfluenceArrayAsBWRgraphic(adapter));
		adapter.getCySwingApplication().addAction(new InfluenceArrayAsGBRgraphic(adapter));
		
		adapter.getCySwingApplication().addAction(new ReachAreaInArray(adapter));
		adapter.getCySwingApplication().addAction(new ReachAreaInAttribute(adapter));
		
		adapter.getCySwingApplication().addAction(new SignEqualityScore(adapter));
		adapter.getCySwingApplication().addAction(new ReverseSignWeightTest(adapter));
		adapter.getCySwingApplication().addAction(new NullWeightTest(adapter));	
	}
}