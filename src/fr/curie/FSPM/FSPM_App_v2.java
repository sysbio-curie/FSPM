package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE  
*/
import org.cytoscape.app.swing.AbstractCySwingApp;
import org.cytoscape.app.swing.CySwingAppAdapter;
/**
 * Cytoscape application
 * launch actions and organize menu
 * @author Daniel.Rovera@curie.fr or @gmail.com
 */
public class  FSPM_App_v2 extends AbstractCySwingApp{
	final static String app="Fading Signal Propagation Model.";
	final static String section1="1 Parameters";
	final static String section2="2 Structural Analysis";
	final static String section3="3 Model Analysis";
	final static String section4="4 Numeric Influence";
	final static String section5="5 Graphic Influence";
	final static String section6="6 Reach Area";
	final static String section7="7 Comparing to Measures";
	final static String section8="8 Modification&Simulation";
	final static String section9="9 Check With All Paths";
	private static CySwingAppAdapter adapter;
	public static CySwingAppAdapter getAdapter(){return adapter;}
	public FSPM_App_v2(CySwingAppAdapter adapter){		
		super(adapter);
		FSPM_App_v2.adapter=adapter;
		String section=section1;
		adapter.getCySwingApplication().addAction(new UpdateInfluenceAttrib(section));
		adapter.getCySwingApplication().addAction(new InputReachParameter(section));
		adapter.getCySwingApplication().addAction(new InputScoreThreshold(section));
		adapter.getCySwingApplication().addAction(new PreselectSrcTgt(section));
		section=section2;
		adapter.getCySwingApplication().addAction(new FromToNodes(section));
		adapter.getCySwingApplication().addAction(new ShortestPathLengths(section));
		adapter.getCySwingApplication().addAction(new SCCinAttribute(section));
		section=section3;
		adapter.getCySwingApplication().addAction(new InfluenceFeatures(section));
		adapter.getCySwingApplication().addAction(new OpenedBackEdges(section));
		adapter.getCySwingApplication().addAction(new SignedDistanceArray(section));
		adapter.getCySwingApplication().addAction(new SignedDistanceList(section));
		adapter.getCySwingApplication().addAction(new NodeListByPath(section));
		adapter.getCySwingApplication().addAction(new EdgeListByPath(section));
		adapter.getCySwingApplication().addAction(new WeightListByPath(section));
		section=section4;
		adapter.getCySwingApplication().addAction(new InfluenceArrayForVisu(section));
		adapter.getCySwingApplication().addAction(new InfluenceArrayForComputing(section));
		adapter.getCySwingApplication().addAction(new InfluenceAsList(section));				
		adapter.getCySwingApplication().addAction(new InfluenceByAttribute(section));
		adapter.getCySwingApplication().addAction(new InfluenceBetweenSubnetworks(section));
		section=section5;
		adapter.getCySwingApplication().addAction(new InfluenceArrayAsBWRgraphic(section));
		adapter.getCySwingApplication().addAction(new InfluenceArrayAsGBRgraphic(section));	
		section=section6;
		adapter.getCySwingApplication().addAction(new ReachAreaInArray(section));
		adapter.getCySwingApplication().addAction(new ReachAreaInAttribute(section));
		section=section7;
		adapter.getCySwingApplication().addAction(new SignEqualityScore(section));
		adapter.getCySwingApplication().addAction(new OpeningEdgeTest(section));
		adapter.getCySwingApplication().addAction(new ReverseSignWeightTest(section));
		adapter.getCySwingApplication().addAction(new NullWeightTest(section));		
		section=section8;
		adapter.getCySwingApplication().addAction(new DeleteNodeAndReconnect(section));
		adapter.getCySwingApplication().addAction(new OpeningEdgeThreshold(section));
		adapter.getCySwingApplication().addAction(new BlockingNodeThreshold(section));
		section=section9;
		adapter.getCySwingApplication().addAction(new SignedDistanceListAllPaths(section));		
		adapter.getCySwingApplication().addAction(new InfluenceListAllPaths(section));
		adapter.getCySwingApplication().addAction(new InfluenceArrayAllPaths(section));
		adapter.getCySwingApplication().addAction(new NodeListAllPaths(section));
		adapter.getCySwingApplication().addAction(new EdgeListAllPaths(section));
		adapter.getCySwingApplication().addAction(new WeightListAllPaths(section));
		
			
	}
}