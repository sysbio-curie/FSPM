package fr.curie.FSPM;

import java.awt.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.text.DecimalFormat;
/**
 * Create a paved window where colors depend on
 * - a state defined by an array of integer,stateNotValue=true
 * - a value from an array of values, 2 options:
 * 1 negative in green, positive in red and zero in black, with a progressive gradation 
 * 2 negative in blue, positive in red and zero in white, with a progressive gradation 
 * Selection of squares writes in text window data of selected squares
 * there is a geometric cooking to adjust square, the result is not always perfect
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class PavingDialog extends JFrame{
	private static final long serialVersionUID = 1L;
	private PavingData pd;
	double maxAbsVal;
	TextBox textbox;
	private JPanel pan;
	private int side;
	boolean stateNotValue;
	boolean blueWhiteRed;
	final int wGap=64;
	class TextBox extends JFrame implements ClipboardOwner{
		private static final long serialVersionUID = 1L;
		private JTextArea jtext;
		TextBox(Window owner, String lineTitle) throws HeadlessException {
			super();
			jtext=new JTextArea(lineTitle+"\r\n");
			add(jtext);
			add(new JScrollPane(jtext),BorderLayout.CENTER);
		}
		public void append(String text){
			jtext.append(text);
			jtext.setCaretPosition(jtext.getDocument().getLength());
			setClipboardContents(jtext.getText());
			}
		public void setClipboardContents(String aString){
		    StringSelection stringSelection = new StringSelection(aString);
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents(stringSelection, this);
		  }
		public void lostOwnership(Clipboard aClipboard, Transferable aContents){}
	}
	public PavingDialog(Window parent,String title,String lineTitle,PavingData pavingData,boolean stateNotValue,boolean redWhiteBlue){ 		
		setTitle(title);
		pd=pavingData;
		this.stateNotValue=stateNotValue;
		this.blueWhiteRed=redWhiteBlue;
		if(pd.values!=null){
			maxAbsVal=0;
			for(int i=0;i<pd.values.length;i++)for(int j=0;j<pd.values[0].length;j++) 
				if(!Double.isNaN(pd.values[i][j])) 
					maxAbsVal=Math.max(maxAbsVal,Math.abs(pd.values[i][j]));	
		}
		pan = new Panel();
		getContentPane().add(pan);
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		side=Math.min(scr.width/2/pd.xNames.length,scr.height/pd.yNames.length);
		setLocation(scr.width-side*pd.xNames.length,0);
		setSize(side*pd.xNames.length,side*pd.yNames.length+wGap);
		textbox=new TextBox(this,lineTitle);
		textbox.setSize(scr.width-side*pd.xNames.length-wGap,scr.height);
		textbox.setTitle(title);
		textbox.setVisible(true);
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){textbox.dispose();dispose();}}); 
	}
	Color getColorBWR(int yi,int xi){
		if(Double.isNaN(pd.values[yi][xi])) return new Color(255,255,255);
		int r=0,g=0,b=0;
		double t=pd.values[yi][xi]/maxAbsVal;
		if(t<0.0){
			r=(int)(255*(1+t));
			g=(int)(255*(1+t));
			b=255;
		}else{
			r=255;
			g=(int)(255*(1-t));
			b=(int)(255*(1-t));
		}	
		return new Color(r,g,b);
	}
	Color getColorGBR(int yi,int xi){
		if(Double.isNaN(pd.values[yi][xi])) return new Color(0,0,0);
		int r=0,g=0,b=0;
		double t=pd.values[yi][xi]/maxAbsVal;
		if(t<0.0){
			r=0;
			g=(int)(255*(-t));
			b=0;
		}else{
			r=(int)(255*t);
			g=0;
			b=0;
		}	
		return new Color(r,g,b);
	}
	class Panel extends JPanel implements MouseMotionListener,MouseListener {		
		private static final long serialVersionUID = 1L;
		private boolean drag=false;
		private int dw,dh;
		private int begX,begY,endX,endY;
		public Panel(){
			addMouseMotionListener(this);
			addMouseListener(this);
			repaint() ;
		}		
		public void mouseDragged (MouseEvent e){
			if(!drag){
				begX=e.getX();begY=e.getY();
				endX=begX;endY=begY;
				drag=true ;
			}
			else endX=e.getX();endY=e.getY() ;			
			repaint() ;
		}
		public String strPre(double inValue){
			DecimalFormat threeDec = new DecimalFormat("0.000");
			String shortString = (threeDec.format(inValue));
			return shortString;
			}

		public void mouseMoved (MouseEvent e){}
		public void mouseReleased(MouseEvent e){
			if(drag) drag=false; else return;			
			int[] range=new int[4];
			range[0]=Math.min(begX,endX)/dw;range[1]=Math.min(Math.max(begX,endX)/dw,pd.xNames.length-1);
			range[2]=Math.min(begY,endY)/dh;range[3]=Math.min(Math.max(begY,endY)/dh,pd.yNames.length-1);
			String text="";
			if(stateNotValue)
				for(int yi=range[2];yi<range[3]+1;yi++)for(int xi=range[0];xi<range[1]+1;xi++)
					text=text+pd.xNames[xi]+"\t"+pd.yNames[yi]+"\t"+pd.stateNames[pd.stateArray[yi][xi]]+"\r\n";					
			else
				for(int yi=range[2];yi<range[3]+1;yi++)for(int xi=range[0];xi<range[1]+1;xi++)
					text=text+pd.xNames[xi]+"\t"+pd.yNames[yi]+"\t"+pd.values[yi][xi]+"\r\n";								
			textbox.append(text);
		}
		public void mouseClicked(MouseEvent v){}
		public void mouseEntered(MouseEvent v){}
		public void mouseExited(MouseEvent v){}
		public void mousePressed(MouseEvent v){}
		private void draw(Graphics g){
			Dimension dim=getSize();
			dw=dim.width/pd.xNames.length;
			dh=dim.height/pd.yNames.length;
			if(stateNotValue)
				for(int xi=0;xi<pd.xNames.length;xi++)for(int yi=0;yi<pd.yNames.length;yi++){
					g.setColor(pd.stateColors[pd.stateArray[yi][xi]]);
					g.fillRect(xi*dw,yi*dh,dw,dh);
					g.setColor(Color.white);
					g.drawRect(xi*dw,yi*dh,dw,dh);
				}
			else
				if(blueWhiteRed){
					for(int xi=0;xi<pd.xNames.length;xi++)for(int yi=0;yi<pd.yNames.length;yi++){					
						g.setColor(getColorBWR(yi,xi));
						g.fillRect(xi*dw,yi*dh,dw,dh);
						g.setColor(Color.black);
						g.drawRect(xi*dw,yi*dh,dw,dh);
					}
				}else{
					for(int xi=0;xi<pd.xNames.length;xi++)for(int yi=0;yi<pd.yNames.length;yi++){					
						g.setColor(getColorGBR(yi,xi));
						g.fillRect(xi*dw,yi*dh,dw,dh);
						g.setColor(Color.white);
						g.drawRect(xi*dw,yi*dh,dw,dh);
					}
				}				
		}
		private void dragRect(Graphics g){
			int x1=Math.min(begX,endX);int x2=Math.max(begX,endX);
			int y1=Math.min (begY,endY);int y2=Math.max(begY,endY);
			g.drawRect(x1,y1,x2-x1,y2-y1) ;
		}
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			draw(g);
			dragRect(g);
		}
	}
}



