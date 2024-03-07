import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Kocka extends JComponent implements MouseListener {

//	protected static int[] cTicks = new int[] {400,300,200,100,100,100,100,100,100,100}; 
	protected static int[][] cTicks = new int[][] {{1,1,1,1,1,1},{300,200,100,100,100,100}}; 

	final int dd=21; // kocka sugara
	final int db=5; // border kockak kozott
	final int dp=4; // potty sugara
	final int d6=2; // 6-osnal plusz potty tavolsag
	final int dbp=9; // border pottyok kozott
	static int dtx=7; // border a zold tabla szelen
	static int dty=7;

/*	final int dd=16; // kocka sugara
	final int db=4; // border kockak kozott
	final int dp=3; // potty sugara
	final int d6=1; // 6-osnal plusz potty tavolsag
	final int dbp=7; // border pottyok kozott
	static int dtx=5; // border a zold tabla szelen
	static int dty=5;
*/
	protected S parent;
	// can throw dice now
	boolean active=true;
	public int[] v = new int[2]; // value
	protected int[] x = new int[] {dd+2+dtx,3*dd+db+2+dtx};
	protected int[] y = new int[] {dd+2+dty,dd+2+dty};
	javax.swing.Timer timer;
	int ticks;
	
	public Kocka(S pParent) {
		parent=pParent;

		setBorder(new javax.swing.border.SoftBevelBorder(1));

		addMouseListener(this);
		setSize(new Dimension(4*dd+db+4+2*dtx,2*dd+4+2*dty));
		v[0]=4; v[1]=3;
	}

	public void dobj() {
		v[0] = S.R.nextInt(6)+1; v[1] = S.R.nextInt(6)+1;
	}

	public void paintComponent(Graphics gg) {
		Graphics2D g = (Graphics2D)gg.create();

		g.setStroke(new BasicStroke(2f, 
							BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		if(active) {
			g.setColor(new Color(20,180,20));
//			g.fillRoundRect(x[0]-dd-dtx,y[0]-dd-dty,4*dd+db+2*dtx,2*dd+2*dty,2*dp,2*dp);
			g.fillRect(x[0]-dd-dtx,y[0]-dd-dty,4*dd+db+2*dtx,2*dd+2*dty);
/*			g.setColor(Color.black);
			g.drawRoundRect(x[0]-dd-dtx,y[0]-dd-dty,4*dd+db+2*dtx,2*dd+2*dty,2*dp,2*dp);
*/
		}
		for (int i=0;i<2;i++) {
			g.setColor(U.kockaColor);
			g.fillRoundRect(x[i]-dd,y[i]-dd,2*dd,2*dd,6*dp,6*dp);
			g.setColor(Color.black);
			g.drawRoundRect(x[i]-dd,y[i]-dd,2*dd,2*dd,6*dp,6*dp);
			if ((v[i]==1)||(v[i]==3)||(v[i]==5)) g.fillOval(x[i]-dp,y[i]-dp,2*dp,2*dp); 
			if ((v[i]==2)||(v[i]==3)||(v[i]==4)||(v[i]==5)) {
				g.fillOval(x[i]-dbp-dp,y[i]-dbp-dp,2*dp,2*dp);
				g.fillOval(x[i]+dbp-dp,y[i]+dbp-dp,2*dp,2*dp);
			}
			if ((v[i]==4)||(v[i]==5)) {
				g.fillOval(x[i]+dbp-dp,y[i]-dbp-dp,2*dp,2*dp);
				g.fillOval(x[i]-dbp-dp,y[i]+dbp-dp,2*dp,2*dp);
			}
			if (v[i]==6) {
				g.fillOval(x[i]-dbp-dp,y[i]-dp,2*dp,2*dp);
				g.fillOval(x[i]+dbp-dp,y[i]-dp,2*dp,2*dp);
				g.fillOval(x[i]-dbp-dp,y[i]-dbp-d6-dp,2*dp,2*dp);
				g.fillOval(x[i]+dbp-dp,y[i]-dbp-d6-dp,2*dp,2*dp);
				g.fillOval(x[i]-dbp-dp,y[i]+dbp+d6-dp,2*dp,2*dp);
				g.fillOval(x[i]+dbp-dp,y[i]+dbp+d6-dp,2*dp,2*dp);
			}
		}
		g.dispose();
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if((active)||(S.arrange)) {
			if (ticks==0) {
				ticks = cTicks[S.animSpeed].length;
				timer = new javax.swing.Timer(cTicks[S.animSpeed][--ticks], taskPerformer);
				timer.start();	
			}
		} else if(!parent.p[parent.ps.current].comp) U.bang("Throw dice only once per turn.");
	}

	ActionListener taskPerformer = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			timer.stop();
			ticks--;
			dobj();
			repaint();
			if (ticks>0) {
				timer = new javax.swing.Timer(cTicks[S.animSpeed][ticks], taskPerformer); 
				timer.start();
			} else {
				parent.b.collect(v[0]+v[1]);
				active=false;
			}
		}
	};
}
