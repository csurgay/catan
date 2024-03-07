import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class Card extends JComponent implements MouseListener {
	S parent;
	
	// type: R-res(fa,tegla...) E-card(lovag,monopol...)
	char type;
	char res;
	
	Deck deck;
	protected boolean selected;
	public boolean facedown; 
	public boolean ausspielt; 
	boolean saveSelect;
	ImageIcon faceIcon, hatlapIcon, bigIcon;
	Card nextCard;
	JLabel face, hatlap, big;
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if((e.getButton()==MouseEvent.BUTTON1)&&(!ausspielt)&&(parent!=null)) {
			if(type=='R') { if(selected) deselect(); else select(); }
			else if(type=='E') { 
				if(facedown) { showCard(); repaint(); }
				int selV = JOptionPane.showOptionDialog(null, 
				"", "Card",
				JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null,
				new Object[] {faceIcon,hatlapIcon},null);
				if(selV==0) { // clicked on the faceup card
					if(res=='u') { // utepito
						JOptionPane.showMessageDialog(null,
						"Take two pieces of road", "Strassenbau",
						JOptionPane.INFORMATION_MESSAGE, null);
						
						adjKetUtat();

					} else if(res=='f') { // felfedezes
						selV = JOptionPane.showOptionDialog(null, 
						"Take first resource card", "Erfindung",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
						parent.resIcons, null);
						
						if(selV!=-1) adjEgyResourcet("ftbjk".charAt(selV));
						
						selV = JOptionPane.showOptionDialog(null, 
						"Take second resource card", "Erfindung",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
						parent.resIcons, null);
						
						if(selV!=-1) adjEgyResourcet("ftbjk".charAt(selV));

					} else if(res=='m') { // monopol
						selV = JOptionPane.showOptionDialog(null, 
						"Which resource do you take from everyone?", "Monopol",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE, null,
						parent.resIcons, null);
						
						if(selV!=-1) addOsszesResourcet("ftbjk".charAt(selV));

					} else if((res=='p')||(res=='o')||(res=='e')||(res=='k')||(res=='t')) {
						parent.p[parent.ps.current].siegpunkt++;

					} else if(res=='l') {
						parent.rablo.active=true;
						U.o(" -- Lovag kartya"); // Report for Java console
						parent.longest.setLongestRoad();
					}
					if((res=='u')||(res=='f')||(res=='m')) {
						parent.p[parent.ps.current].eDeck.removeFaceup(res);
						parent.pakli.teddvissza(res);
					}
					ausspielt=true;
				} else { // clicked on the facedown card
					hideCard();
				}
			}
			parent.repaint();
		} else if((e.getButton()==MouseEvent.BUTTON3)&&(type=='E')&&
			((res=='l')||(res=='u')||(res=='f')||(res=='m'))) {
			int selV = JOptionPane.showOptionDialog(null, 
			"", "Card",
			JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null,
			new Object[] {bigIcon},null);
		}
	}

	public void select() {
		selected=true;
		deck.changedSelectedFaceup();
		deck.alignCards();
	}

	public void deselect() {
		selected=false;
		deck.changedSelectedFaceup();
		deck.alignCards();
	}

	public Card(char pType, char pRes, Deck pDeck, S pParent) {
		setLayout(null);
		addMouseListener(this);
		type=pType;
		res=pRes;
		deck=pDeck;
		selected=false;
		ausspielt=false;
		parent=pParent;
		setSize(58,91);
		if(type=='R') { 
			faceIcon=ImageLoader.getResourceIcon(res);
			hatlapIcon=ImageLoader.getResourceIcon('h');
			bigIcon=ImageLoader.getBigResourceIcon(res);
		} 
		else if(type=='E') {
			faceIcon=ImageLoader.getCardIcon(res);
			hatlapIcon=ImageLoader.getCardIcon('h');
			bigIcon=ImageLoader.getBigCardIcon(res);
			if(res=='u') setToolTipText("Strassenbau"); 
			else if(res=='f') setToolTipText("Erfindung");
			else if(res=='m') setToolTipText("Monopol");
			else if(res=='l') setToolTipText("Knight");
			else if((res=='p')||(res=='o')||(res=='e')||(res=='k')||(res=='t')) setToolTipText("Siegpunkt");
		}
		face=new JLabel(faceIcon); face.setBounds(0,0,58,91); 
		hatlap=new JLabel(hatlapIcon); hatlap.setBounds(0,0,58,91);
		facedown=false; add(face); hideCard();
	}

	public void showCard() {
		if(facedown) { facedown=false; remove(hatlap); add(face); }
	}
	
	public void hideCard() {
		if(!facedown) { facedown=true; remove(face); add(hatlap); }
	}
	
	public void paintComponent(Graphics g) {
		int q=13;
		Ellipse2D.Double circle = new 
			Ellipse2D.Double(-q,-q,58+2*q,91+2*q);
		g.setClip(circle);
	}
	
	public void adjKetUtat() {
		if(parent.p[parent.ps.current].noUt<Player.maxNoUt) {
			if(parent.p[parent.ps.current].ut.invisible)
				parent.p[parent.ps.current].noUt++; 
			parent.p[parent.ps.current].ut.invisible=false;
		}
		if(parent.p[parent.ps.current].noUt<Player.maxNoUt) {
			if(parent.p[parent.ps.current].ut2.invisible)
				parent.p[parent.ps.current].noUt++; 
			parent.p[parent.ps.current].ut2.invisible=false; 
			parent.p[parent.ps.current].vanUt2=true; 
		}
	}
	
	public void adjEgyResourcet(char pRes) {
		parent.p[parent.ps.current].deck.addCard('R',pRes);
		repaint();
	}
	
	public void addOsszesResourcet(char pRes) {
		int n=0;
		for(int i=1;i<=S.noPlayer;i++) if(i!=parent.ps.current)
			n+=parent.p[i].deck.removeRes(pRes);
		for(int i=0;i<n;i++)
			parent.p[parent.ps.current].deck.addCard('R',pRes);
	}
}