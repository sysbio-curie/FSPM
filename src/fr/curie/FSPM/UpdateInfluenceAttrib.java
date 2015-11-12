package fr.curie.FSPM;
/*
Fading Signal Propagation Model Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
 */
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
/**
 * Update Influence Attribute WEIGHT at +1 or -1
 * from an attribute, generally interaction
 * 3 possible value for attribute
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class UpdateInfluenceAttrib extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	final public static String title="Update Weigth Attribute";
	private CySwingAppAdapter adapter;
	public UpdateInfluenceAttrib(CySwingAppAdapter adapter){
		super(title,adapter.getCyApplicationManager(),"network",adapter.getCyNetworkViewManager());
		setPreferredMenu(Ttls.app+Ttls.chap2);
		this.adapter = adapter;
	}		
	String attribName="WEIGHT";
	String[] label={"Attribute to use for influence",
			"Attribute Values for Activation:",
			"Value 1 for Activation",
			"Value 2 for Activation",
			"Value 3 for Activation",
			"Attribute Values for Inhibition:",
			"Value 1 for Inhibition",
			"Value 2 for Inhibition",
			"Value 3 for Inhibition"};
	Double[] values={0.0,
			1.0,
			1.0,
			1.0,
			-1.0,
			-1.0,
			-1.0};
	String nothing="Nothing";
	String selected="interaction";
	CyNetwork network;
	ArrayList<String> fields;	
	ArrayList<ArrayList<String>> datas;
	ArrayList<String> data;
	void fillAttributesValues(){
		fields=new ArrayList<String>();	
		datas=new ArrayList<ArrayList<String>>();
		Iterator<CyColumn> columnsIterator=network.getDefaultEdgeTable().getColumns().iterator();
		while(columnsIterator.hasNext()){
			CyColumn col = columnsIterator.next();
			if(col.getType()==String.class){
				fields.add(col.getName());
				datas.add(new ArrayList<String>());	 
			}
		}
		for(int i=0;i<fields.size();i++){
			for(CyEdge edge:network.getEdgeList()){		 
				String attrStr=network.getRow(edge).get(fields.get(i),String.class);
				if(!datas.get(i).contains(attrStr)&&(attrStr!=null)) 
					datas.get(i).add(attrStr); 
			}
		}		 	 
		for(int i=0;i<datas.size();i++){
			Collections.sort(datas.get(i));
			datas.get(i).add(0,nothing);
		}
	}	
	public int update(){
		int unknownNb=0;
		for(CyEdge edge:network.getEdgeList()){
			String attrStr=network.getRow(edge).get(data.get(0),String.class);
			if(network.getDefaultEdgeTable().getColumn(attribName)==null) network.getDefaultEdgeTable().createColumn(attribName, Double.class, false);
			int ai=data.indexOf(attrStr);
			if(ai==-1){
				unknownNb++;
				network.getRow(edge).set(attribName,0.0);
			}
			else
				network.getRow(edge).set(attribName,values[ai]);
		}
		return unknownNb;
	}	
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=adapter.getCyApplicationManager();
		CySwingApplication swingApplication=adapter.getCySwingApplication();
		network=applicationManager.getCurrentNetwork();
		fillAttributesValues();
		data=new ArrayList<String>();
		ComboBoxes dialog=new ComboBoxes(swingApplication.getJFrame(), title,label,fields,selected,datas);
		if(dialog.launchDialog(data)){
			int unknown=update();
			if(unknown!=0) JOptionPane.showMessageDialog(swingApplication.getJFrame(), unknown+" edges with unknown influence",
					unknown+" edges taken as disconnected in influence evaluation",JOptionPane.WARNING_MESSAGE);
		}
	}
}
