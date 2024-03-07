import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Haz extends JComponent implements MouseListener {

	static int[][] hvPx = new int[][] {
		{0,0,0,0,0,0,0},{3,10,17,17, 3, 3, 3},{1,7,13,13,25,25, 1}};
	static int[][] hvPy = new int[][] {
		{0,0,0,0,0,0,0},{10,3,10,19,19,19,19},{7,1, 7,11,11,22,22}};
	static String[] hvString = new String[] {"semmi", "ftbj","bbkkk"};
	static String[] hvName = new String[] {"semmi", "house","town"};
	static Cursor[] cursor=new Cursor[3]; 
	static Cursor oldcursor; 
	public S parent; 
	static Polygon[] hvP = new Polygon[3];
	public static boolean moves=false;
	public static Haz movedHaz = new Haz(0,0,false,null);
	public boolean nonBoard, frameOnly, movable;	

	Junction junc;
	public Player playerPointer;
	public int type; // 1-haz, 2-varos
	public int player;
	public int constrainType;
	
	public int hanyadik;
	
	public static void init(Settlers pSettlers) {
		cursor[1] = Toolkit.getDefaultToolkit().createCustomCursor(
			pSettlers.getIcon("hazCursor.gif").getImage(),new Point(6,10),"haz");
		cursor[2] = Toolkit.getDefaultToolkit().createCustomCursor(
			pSettlers.getIcon("varosCursor.gif").getImage(),new Point(10,10),"varos");
		hvP[1] = new Polygon(); 
		hvP[2] = new Polygon(); 
		for(int i=1;i<=2;i++) for(int k=0;k<hvPx[i].length;k++) 
			hvP[i].addPoint(hvPx[i][k],hvPy[i][k]);
	}

	public Haz(int pType, int pPlayer, boolean pNonBoard, S pparent) {
		addMouseListener(this);
		setSize(new Dimension(27,24));
		type=pType; player=pPlayer; nonBoard=pNonBoard; 
		frameOnly=false; movable=true; if(parent==null) parent=pparent;
		hanyadik=0;
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {
		// PlayerSelect varos
		if((!movable)&&(player<=S.noPlayer)&&(parent!=null)) {
			for(int i=1;i<=S.noPlayer;i++) {
				parent.ps.hazak[i].frameOnly=true;
				parent.p[i].setVisible(false);
			}
			parent.ps.hazak[player].frameOnly=false;
			parent.p[player].setVisible(true);
			parent.repaint();
		// arakKartya es nincs ott semmi, venni akar
		} else if((!moves)&&(movable)&&(nonBoard)&&(type==0)) {
			String hvS=hvString[constrainType];
			playerPointer.deck.saveSelect();
			if (!playerPointer.deck.isSelect(hvS)) {
				if(playerPointer.deck.isMegvanBenne(hvS)) {
					playerPointer.deck.select(hvS);
					parent.repaint();
				}
			}
		}
	}
	public void mouseExited(MouseEvent e) {
		// PlayerSelect varosok kozul csak current-et visszaallitani
		if((!movable)&&(player<=S.noPlayer)&&(parent!=null)) {
			for(int i=1;i<=S.noPlayer;i++) {
				parent.ps.hazak[i].frameOnly=true;
				parent.p[i].setVisible(false);
			}
			parent.ps.hazak[parent.ps.current].frameOnly=false;
			parent.p[parent.ps.current].setVisible(true);
			parent.repaint();
		// arakKartya es nincs ott semmi
		} else if((!moves)&&(movable)&&(nonBoard)&&(type==0)) {
			playerPointer.deck.restoreSelect();
			parent.repaint();
		}
	}
	public void mouseClicked(MouseEvent e) {

		// mozgathato h/v, masik nem mozog
		if((!moves)&&(movable)) {
			// van ott valami (akar tablai akar arakKartya)
			if(type>0) {
				if((nonBoard)||(S.arrange)) {
					moves=true;
					setVisible(false); 
					oldcursor=getCursor(); parent.setCursor(cursor[type]);
					movedHaz.type = type; movedHaz.player = player;
					if(!nonBoard) {
						parent.p[player].siegpunkt-=type;
						parent.p[player].noHazVaros[type]--;		
					}
					type=0;
					setVisible(true); 
					parent.repaint();
				}
			// arakKartya es nincs ott semmi
			} else if((nonBoard)&&(type==0)) {
				String s[] = new String[1];
				String hvS=hvString[constrainType];
				if (playerPointer.deck.isSelect(hvS)) {
					if(playerPointer.noHazVaros[constrainType]<Player.maxNoHazVaros[constrainType]) {
						type=constrainType;
						playerPointer.noHazVaros[constrainType]++;
						playerPointer.deck.removeSelect();
						parent.repaint();
					} else {
						U.bang("Cannot build more than "
						+new Integer(Player.maxNoHazVaros[constrainType]).toString()
						+" "+hvName[constrainType]+"s");
					}
				}
			}
		}
		// mozgoval mozgathatora kattintottak
		else if((moves)&&(movable)) {
			// ha tablai haz es nincs szomszed, van ilyen ut ide(vagy jatek eleje), es nagyobb kisebbre
			if(!nonBoard) { 
				if(junc!=null) if((S.arrange)||((!junc.vanszomszed())&&
					((junc.vanideut(movedHaz.player))||(S.noRounds<3))&&(type+1==movedHaz.type))) {
					if(type==1) parent.p[player].noHazVaros[1]--;
					type=movedHaz.type; player=movedHaz.player;
					parent.setCursor(Haz.oldcursor); 
					Haz.moves = false;
					parent.p[player].siegpunkt++;
					parent.repaint();
					if(type==1) hanyadik=++parent.p[player].hanyadikHaza;
				}
			// ha nem tablai haz es olyan szinu, oda valo, es nincs ott semmi
			} else if((movedHaz.player==player)&&(movedHaz.type==constrainType)
				&&(type==0)) {
				type=movedHaz.type; player=movedHaz.player;
				parent.setCursor(Haz.oldcursor); 
				Haz.moves = false;
				// letettuk, de becsereljuk kartyara
				if(type==1) {
					playerPointer.deck.addCard('R','f');
					playerPointer.deck.addCard('R','t');
					playerPointer.deck.addCard('R','b');
					playerPointer.deck.addCard('R','j');
				} else if (type==2) { 
					playerPointer.deck.addCard('R','b');
					playerPointer.deck.addCard('R','b');
					playerPointer.deck.addCard('R','k');
					playerPointer.deck.addCard('R','k');
					playerPointer.deck.addCard('R','k');
				}
				type=0;
				parent.repaint();
			}
		}
	}

	public void paintComponent(Graphics gg) {
		Graphics2D g = (Graphics2D)gg.create();
		g.setStroke(new BasicStroke(1.5f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		if ((type>0)&&(player>0)) {
			g.setColor(U.playerColor[player]); 
			g.fillPolygon(hvP[type]); 
			g.setColor(Color.black); 
			g.drawPolygon(hvP[type]);
			if((movable)&&(hanyadik>0)&&S.hanyadik) {
				char[] c = new char[1];
				new Integer(hanyadik).toString().getChars(0,1,c,0);
				if(type==1) g.drawChars(c,0,1,7,17); else g.drawChars(c,0,1,4,19);
			}
			if((!movable)&&(parent!=null)) {
				if(!frameOnly) {
					g.setColor(Color.yellow); 
					g.drawPolygon(hvP[type]);
					g.setColor(Color.black);
				}
				char[] c = new char[2];
				int e=parent.p[player].siegpunkt;
				if (e>9) {
					new Integer(e).toString().getChars(0,2,c,0);
					g.drawChars(c,0,2,1,19);
				} else {
					new Integer(e).toString().getChars(0,1,c,0);
					g.drawChars(c,0,1,4,19);
				}
				if(S.internals) {
					e=parent.p[player].noHazVaros[1];
					new Integer(e).toString().getChars(0,1,c,0);
					g.drawChars(c,0,1,9,21);
					e=parent.p[player].noHazVaros[2];
					new Integer(e).toString().getChars(0,1,c,0);
					g.drawChars(c,0,1,14,21);
					e=parent.p[player].noUt;
					new Integer(e).toString().getChars(0,1,c,0);
					g.drawChars(c,0,1,19,21);
				}
				if(parent.p[player].comp)
					g.drawChars(new char[]{'c'},0,1,18,21);
			}
		}
		g.dispose();
	}
}
