import java.awt.event.*;
import javax.swing.*;

public class PlayerSelect extends JComponent implements MouseListener {

	S parent;
	Haz[] hazak = new Haz[7];
	int current;
	
	public PlayerSelect(S pParent) {
		parent=pParent;
		current=1;
		setSize(255,45);
		addMouseListener(this);
		setBorder(new javax.swing.border.BevelBorder(1));
		for(int i=1;i<=S.noPlayer;i++) {
			hazak[i]=new Haz(2,i,true,parent); hazak[i].movable=false;
			add(hazak[i]);
//			hazak[i].setLocation(i*60-40,10);
			hazak[i].setLocation(i*42-32,10);
			hazak[i].setSize(42,24);
		}
		activateCurrent();
	}
	
	boolean masodikKorVege;
	
	public void init() {
		for(int i=0;i<7;i++) if(hazak[i]!=null) remove(hazak[i]);
		current=S.beginnerPlayer;
		masodikKorVege=false;
		backwards=false;
		for(int i=1;i<=S.noPlayer;i++) {
			hazak[i]=new Haz(2,i,true,parent); hazak[i].movable=false;
			add(hazak[i]);
//			hazak[i].setLocation(i*60-40,10);
			hazak[i].setLocation(i*42-32,10);
			hazak[i].setSize(42,24);
		}
		activateCurrent();
	}

	public void activateCurrent() {
		activateThis(current);
	}

	public void activateThis(int pCurrent) {
		for(int i=1;i<=S.noPlayer;i++) {
			hazak[i].frameOnly=true;
		}
		hazak[pCurrent].frameOnly=false;
	}

	public void next() {
		if(current!=0) hazak[current].frameOnly=true;
		current++;
		if(current>S.noPlayer) {current=1;}
		if(current==S.beginnerPlayer) {S.noRounds++;} 
		hazak[current].frameOnly=false;
		parent.repaint();
	}

	public void previous() {
		hazak[current].frameOnly=true;
		current--;
		if(current<1) {current=S.noPlayer;}
		if(masodikKorVege) {S.noRounds++;}
		if(current==S.beginnerPlayer) {masodikKorVege=true;}
		hazak[current].frameOnly=false;
		parent.repaint();
	}

	public void mouseEntered(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {
		// PlayerSelect varosok kozul csak current-et visszaallitani
		for(int i=1;i<=S.noPlayer;i++) {
			parent.ps.hazak[i].frameOnly=true;
			parent.p[i].setVisible(false);
		}
		parent.ps.hazak[parent.ps.current].frameOnly=false;
		parent.p[parent.ps.current].setVisible(true);
		parent.repaint();
	}

	boolean backwards;
	
	public Player nextPlayer() {
		
		if(parent.rablo.active)	// Just az assertion error report to Java console
			U.o("Rablo active error");
		
		if(current!=0) {
			if(!S.openHands) parent.p[current].deck.hideCards();
			for(int i=1;i<=S.noPlayer;i++) parent.p[i].setVisible(false);
		}
		
		int old=current;
		if (backwards) previous(); else next();
		
		// elso kor utan forditva vissza
		if((S.noRounds==2)&&(!backwards)) {
			backwards=true; previous();
		}
		// forditva vissza kor vege 
		if ((backwards)&&(S.noRounds==3)) {
			backwards=false; S.noRounds--; next();
			// laposztas
			for(int v=2;v<=12;v++) if(v!=7) parent.b.collect(v);
		} 
		// elso ket korben adunk egy hazat es egy utat
		if(S.noRounds<=2) {
			parent.p[current].ut.invisible=false; parent.p[current].noUt++;
			parent.p[current].haz.setVisible(true);
			parent.p[current].haz.type=1; parent.p[current].noHazVaros[1]++;
			parent.p[current].varos.type=0;
			parent.p[current].varos.setVisible(true);
		}
		parent.p[current].setVisible(true);
		parent.p[current].deck.showCards();
		parent.p[current].deck.changedSelectedFaceup();
		parent.p[current].eDeck.changedSelectedFaceup();
		
		if(S.noRounds>2) {
			parent.kocka.active=true;
		} 

		activateCurrent();
		parent.repaint();

		return parent.p[current];
	}
	
	public boolean allComp() {
		boolean result=true;
		for(int i=1;i<S.noPlayer;i++) if(!parent.p[i].comp) result=false;
		return result;
	}
}
