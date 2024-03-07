import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Rablo extends JComponent implements MouseListener {
	// if it is in motion right now
	public boolean moves=false;
	// if can be moved now
	public boolean active=false;
	Cursor cursor, oldcursor; S parent;
	Polygon talpV, talpH;

	public Rablo(S pParent, Settlers pSettlers) {
		parent = pParent;
		setSize(23,47);
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(
			pSettlers.getIcon("rabloCursor.gif").getImage(),new Point(15,18),"rablo");
		addMouseListener(this);
		talpV=new Polygon();talpV.addPoint(5,37);talpV.addPoint(0,42);talpV.addPoint(0,46);
		talpV.addPoint(22,46);talpV.addPoint(22,42);talpV.addPoint(17,37);
		talpH=new Polygon();talpH.addPoint(37,5);talpH.addPoint(42,0);talpH.addPoint(46,0);
		talpH.addPoint(46,22);talpH.addPoint(42,22);talpH.addPoint(37,17);
	}
	
	public int[] getRablosak(int pPl) {
		int[] van=new int[] {0,0,0};
		int darab=0;
		if(parent.b.rabloTile.type=='N') for(int i=0;i<6;i++) 
			if(parent.b.rabloTile.juncs[i].haz.type>0) {
			int pl=parent.b.rabloTile.juncs[i].haz.player;
			if((van[0]!=pl)&&(van[1]!=pl)&&(pl!=pPl)
				&&(parent.p[pl].deck.noCards()>0)) van[darab++]=pl;
		}
		int[] result = new int[darab];
		for(int i=0;i<darab;i++) result[i]=van[i];
		return result;
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if((active)||(S.arrange)) {
			active=false;
			moves=true; 
			setVisible(false); 
			parent.repaint();
			oldcursor=getCursor(); parent.setCursor(cursor);
			parent.b.rabloTile.hasRablo=false;
		} else U.bang("Rablo can only be moved after throwing 7 with the dice or playing out a Knight.");
	}

	public void paintComponent(Graphics g) {
		if(active) {
			setSize(47,23);
			g.fillOval(0,3,18,16); g.fillOval(11,0,32,22); 
			g.fillRect(42,0,3,22); g.fillPolygon(talpH);
		} else {
			setSize(23,47);
			g.fillOval(3,0,16,18); g.fillOval(0,11,22,32); 
			g.fillRect(0,42,22,3); g.fillPolygon(talpV);
		}
	}
	
	public void throwHalf() {
		for(int i=1;i<=S.noPlayer;i++) {
			Deck d=parent.p[i].deck; int n=d.noCards()/2;
			if(d.noCards()>7) for(int j=0;j<n;j++) {
				int noc = d.noCards();
				Card[] cards=new Card[noc];
				int selV=0;
				ImageIcon[] icons=new ImageIcon[noc];
				int k=0;
				Card c=d.firstCard; while(c!=null) {
					cards[k]=c; icons[k++]=c.faceIcon; c=c.nextCard;}
				if(parent.p[i].comp) {
					selV=S.R.nextInt(noc);
				} else {
					S.aiSuspend=true;
					do { selV=JOptionPane.showOptionDialog(null, 
						"", "Throw half of your cards",
						JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,
						ImageLoader.playerIcon[i],
						icons,null);
					} while(selV==-1);
					S.aiSuspend=false;
				}
				parent.p[i].deck.removeCard(cards[selV]);
			}
		} 
	}		
	
	public void robPlayer() {
		int[] pls = parent.rablo.getRablosak(parent.ps.current);
		if(pls.length==0) {
			if(parent.p[parent.ps.current].comp) {;}
			else JOptionPane.showMessageDialog(null,"There is no player to rob",
				"Robber",JOptionPane.INFORMATION_MESSAGE,null);
		} else {
			Object[] plsO = new Object[pls.length];
			for(int i=0;i<pls.length;i++) {
				plsO[i]=ImageLoader.playerIcon[pls[i]];
			}
			int selV;
			if(parent.p[parent.ps.current].comp) {
				selV=S.R.nextInt(pls.length);
			} else {
				selV = JOptionPane.showOptionDialog(null, 
				"Which player to rob", "Robber",
				JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null,
				plsO,null);
			}
			if(selV!=-1) {
				char randomCard=parent.p[pls[selV]].deck.removeRandom();
				parent.p[parent.ps.current].deck.addCard('R',randomCard);
				repaint();
			} 
		}

	}
}
