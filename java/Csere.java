// Storing csere cards for Comp player, 
// and cserePanel dialog

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Csere extends JComponent implements ActionListener {
	CompAction ca;

	char[] missing = new char[2];
	char[] cards;
	
	public char[] maradek(String pS,String pSS) {
		String[] s = new String[] {pS};
		for(int i=0;i<pSS.length();i++) {ca.pl.deck.remove(s,pSS.charAt(i));}
		return s[0].toCharArray();
	}
	public void mikell(String pS,char pC,char pD) {
		cards = maradek(ca.pl.deck.getAllString(),pS);
		missing[0]=pC; missing[1]=pD;
	}
	public char[] getCsereCards() {
		char[] res=null;
		int db=4;
		if(ca.parent.b.vanKikotoje(ca.currentPlayer,'3')) db=3;
		if((ca.parent.b.vanKikotoje(ca.currentPlayer,'f'))&&(vanDupla('f',cards))) 
			res=new char[]{'f','f'};
		if((ca.parent.b.vanKikotoje(ca.currentPlayer,'t'))&&(vanDupla('t',cards))) 
			res=new char[]{'t','t'};
		if((ca.parent.b.vanKikotoje(ca.currentPlayer,'b'))&&(vanDupla('b',cards))) 
			res=new char[]{'b','b'};
		if((ca.parent.b.vanKikotoje(ca.currentPlayer,'j'))&&(vanDupla('j',cards))) 
			res=new char[]{'j','j'};
		if((ca.parent.b.vanKikotoje(ca.currentPlayer,'k'))&&(vanDupla('k',cards))) 
			res=new char[]{'k','k'};
		if((res==null)&&(cards.length>=db)) {
			res=new char[db];
			for(int i=0;i<db;i++) res[i]=cards[i];
		} 
		return res;
	}
	public boolean vanDupla(char pRes,char[] pC) {
		boolean van1=false; boolean van2=false;
		for(int i=0;i<pC.length;i++) if(pC[i]==pRes) if(van1) van2=true; else van1=true;
		return van2;
	}
	public void removeCsereCards(char[] pCs) {
		for(int i=0;i<pCs.length;i++) removeCsereChar(pCs[i]);
	}
	public void removeCsereChar(char pCh) {
		char[] res=new char[cards.length-1];
		int n=-1; for(int i=0;i<cards.length;i++) if(cards[i]==pCh) n=i;
		for(int i=0;i<n;i++) res[i]=cards[i];
		for(int i=n;i<cards.length-1;i++) res[i]=cards[i+1];
		cards=res;
	}
	
	public Csere(CompAction pCa) {
		ca=pCa;
		for(int i=0;i<6;i++) {
			offerButt[i]=new JButton("Remove"); // ez lesz "TakeIt"
			offerButt[i].addActionListener(this);
		}
		okButt = new JButton("Submit"); // ez lesz "Pass"
		okButt.addActionListener(this);
		haz1 = new Haz(2,0,true,null);
		haz1.movable=false; haz1.setLocation(40,10);
		haz2 = new Haz(2,0,true,null);
		haz2.movable=false; haz2.setLocation(215,10); haz2.setVisible(false);
		init();
	}
	
	
	// itt kezdodik a cserePanel resz
	public int nOffers;
	String[] mit = new String[6];
	char[] mire = new char[6];
	JButton[] offerButt = new JButton[6];
	JButton okButt;
	Haz haz1, haz2;

	public void init() {
		removeAll(); add(okButt); add(haz1); add(haz2); haz2.player=0;
		nOffers=0;
		setVisible(false);
		for(int i=0;i<6;i++) offerButt[i].setText("Remove");
		okButt.setText("Submit");
	}

	public void addOffer(int pPlayer, String pMit, char pMire) {
		haz1.player=pPlayer;
		Card card;
		mit[nOffers]=pMit; mire[nOffers]=pMire; 
		for(int i=0;i<pMit.length();i++) {
			card = new Card('R',pMit.charAt(i),null,null);
			card.showCard(); card.setLocation(30*i+20,nOffers*100+40); add(card);
		}
		card = new Card('R',pMire,null,null);
		card.showCard(); card.setLocation(200,nOffers*100+40); add(card);
		offerButt[nOffers].setBounds(300,nOffers*100+70,100,40); add(offerButt[nOffers]);
		nOffers++;
		okButt.setBounds(160,nOffers*100+60,100,40);
		setBounds(450,300-nOffers*50,430,nOffers*100+120);
	}
	
	public void removeOffer(int sor) {
		removeAll(); add(okButt); add(haz1); add(haz2);
		for(int i=sor;i<nOffers-1;i++) {
			mit[i]=mit[i+1]; mire[i]=mire[i+1];
		}
		int k=nOffers-1; nOffers=0;
		for(int i=0;i<k;i++) addOffer(haz1.player,mit[i],mire[i]);
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton butt = (JButton)e.getSource();
		for(int i=0;i<6;i++) if(butt==offerButt[i])
			if(butt.getText().equals("Remove")) {
				removeOffer(i);
				if(nOffers==0) setVisible(false);
			} else { // TakeIt
				if(ca.parent.p[haz2.player].deck.isMegvanBenne(new Character(mire[i]).toString())) {
					ca.parent.p[haz1.player].deck.select(mit[i]);
					ca.parent.p[haz1.player].deck.removeSelect();
					ca.parent.p[haz1.player].deck.addCard('R',mire[i]);
					ca.parent.p[haz2.player].deck.removeFaceup(mire[i]);
					for(int j=0;j<mit[i].length();j++) 
						ca.parent.p[haz2.player].deck.addCard('R',mit[i].charAt(j));
					ca.parent.p[haz2.player].setVisible(false);
					if(!S.openHands) ca.parent.p[haz2.player].deck.hideCards();
					ca.parent.p[haz1.player].deck.showCards();
					ca.parent.p[haz1.player].setVisible(true);
					ca.parent.ps.activateThis(haz1.player);
					ca.parent.repaint();
					init();
				} else {
					U.bang("Sorry, you do not possess the card required by this offer");
				}
			}
		if(butt==okButt) 
			if(butt.getText().equals("Submit")) {
				for(int i=0;i<6;i++) offerButt[i].setText("TakeIt");
				okButt.setText("Pass");
				nextSubmit(haz1.player);
			} else { // Pass
				nextSubmit(haz2.player);
			}
	}
	
	public void nextSubmit(int prev) {
		ca.parent.p[prev].setVisible(false);
		if(!S.openHands) ca.parent.p[prev].deck.hideCards();
		haz2.player=prev+1; if(haz2.player>S.noPlayer) haz2.player=1;
		ca.parent.p[haz2.player].deck.showCards();
		ca.parent.p[haz2.player].setVisible(true);
		haz2.setVisible(true);
		ca.parent.ps.activateThis(haz2.player);
		if(ca.parent.p[haz2.player].comp) {
			okButt.setEnabled(false);
			ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], csereDecideAction); 
			ca.timer.start();
		} 
		ca.parent.repaint();
		if(haz2.player==haz1.player) { // korbeert
			init();
		}
	}
	
	ActionListener csereDecideAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ca.timer.stop(); double dv=0.0; int v;
//			U.o("decide");
			double diffMax=0.0; int offer=-1; for(int i=0;i<nOffers;i++) {
				double diff=0.0; for(int j=0;j<mit[i].length();j++) { 
					v=ca.parent.b.getPlayerSum(haz2.player, ImageLoader.mezonevek[
						ImageLoader.getPos(mit[i].charAt(j),ImageLoader.resnevek)].charAt(0));
					dv=new Integer(v).doubleValue();
//					U.o("off "+new Double(dv).toString());
					if(dv==0.0) diff+=100.0; else diff+=36.0/dv;
				}
				v=ca.parent.b.getPlayerSum(haz2.player, ImageLoader.mezonevek[
					ImageLoader.getPos(mire[i],ImageLoader.resnevek)].charAt(0));
				dv=new Integer(v).doubleValue();
//				U.o("req "+new Double(dv).toString());
				if(dv==0.0) diff-=100.0; else diff-=36.0/dv;
				if(diff>diffMax) {diffMax=diff;offer=i;}
			}
//			U.o("diff "+new Double(diffMax).toString());
			if(diffMax>5.0) { // miert pont 5? :)
				if(ca.parent.p[haz2.player].deck.isMegvanBenne(new Character(mire[offer]).toString())) {
					okButt.setEnabled(true); offerButt[offer].doClick();
				} else {
					okButt.setEnabled(true); okButt.doClick();
				}
			} else {
				okButt.setEnabled(true); okButt.doClick();
			}
		}
	};

	public void paintComponent(Graphics gg) {
		Graphics2D g = (Graphics2D)gg.create();
//		g.setStroke(new BasicStroke(6.0f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
//		g.setColor(U.playerColor[player]);
//		if(!invisible) g.drawLine(0,3,32,3);
		g.setColor(new Color(20,180,20));
		g.fillRect(0,0,1000,900);
		g.dispose();
	}
}
