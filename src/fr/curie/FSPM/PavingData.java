package fr.curie.FSPM;


import java.awt.Color;
/**
 * Data structure for Paving Dialog used too for displaying arrays
 * xNames: column names
 * yNames: row names
 * stateNames: state names for discrete visualization
 * stateAbbrev: short names of states to displaying in text window
 * stateArray: array of states for discrete visualization
 * stateColors: colors used for displaying states
 * values: continue states values for continue visualization from blue to red (negative to positive)
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class PavingData{
	public String[] xNames;
	public String[] yNames;
	public String[] stateNames;
	public String[] stateAbbrev;
	public int[][] stateArray;
	public Color[] stateColors;
	public double[][] values;
	public PavingData(){}
}
