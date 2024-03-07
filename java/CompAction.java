import java.awt.event.*;

public class CompAction {
	
	S parent;
	CompTurn ct;
	Csere cs;

	public CompAction(S pParent,CompTurn pCT) {
		parent=pParent;
		ct = pCT;
		cs=new Csere(this);
	}
	
	javax.swing.Timer timer;
	
	int currentPlayer, turnCounter, actionCounter;
	boolean csinaltValamit;
	Player pl;
	Card cardToPlayOut;
	ActionListener returnTurnAction;
	Junction firstTwoHaz;
	
	int[] kisTime=new int[]{1,1};
	int[] nagyTime=new int[]{1,800}; 
	int[] kockaTime=new int[]{50,1500};
	
	ActionListener firstTwoRoundsTurnAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			timer.stop();
			turnCounter++;
			if(turnCounter==1) {
				actionCounter=0; returnTurnAction=firstTwoRoundsTurnAction;
				timer = new javax.swing.Timer(nagyTime[S.animSpeed], hazEpitAction);
				timer.start();
			} else if(turnCounter==2) {
				actionCounter=0; returnTurnAction=firstTwoRoundsTurnAction;
				timer = new javax.swing.Timer(nagyTime[S.animSpeed], utEpitAction);
				timer.start();
			} else if(turnCounter==3) {
				timer.stop();
				if(parent.ps.nextPlayer().comp) ct.turn(parent.ps.current);
				else parent.getButton("kovetkezo").setEnabled(true);
			} 
			parent.repaint();
		}
	};
	ActionListener kockaAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			timer.stop();
			parent.kocka.mouseClicked(null);
			timer = new javax.swing.Timer(kockaTime[S.animSpeed], ct.turnAction);
			timer.start();
		}
	};
	ActionListener rabloReplaceAction = new ActionListener() {
		Tile tile,tile2;
		public void actionPerformed(ActionEvent evt) {
			// if other dialog goes on (other player throws half of cards), this waits
			if(S.aiSuspend) return;
			actionCounter++;
			if(actionCounter==1) {
				tile2=parent.b.rabloTile;
				parent.rablo.mouseClicked(null);
			} else if(actionCounter==2) {
				tile = AI.getTileForRablo(currentPlayer,tile2);
				parent.mouseClicked(new MouseEvent(parent,0,0,0,
				tile.x,tile.y,1,false,MouseEvent.BUTTON1));
			} else if(actionCounter==3) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], ct.turnAction);
				timer.start();
			} 
			parent.repaint();
		}
	};
	ActionListener strassenbauAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			actionCounter++;
			if(actionCounter==1) {
				pl.eDeck.eloreCard(cardToPlayOut); parent.repaint();
			} else if(actionCounter==2) {
				cardToPlayOut.adjKetUtat();
				parent.repaint();
			} else if(actionCounter==3) {
				pl.eDeck.removeFaceup(cardToPlayOut.res);
				parent.pakli.teddvissza(cardToPlayOut.res);
			} else if(actionCounter==4) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], ct.turnAction);
				timer.start();
			}
		}
	};
	ActionListener lapotVeszAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			actionCounter++;
			if(actionCounter==1) {
				pl.deck.select("bjk");
			} else if(actionCounter==2) {
				pl.ew.mouseClicked(null);
			} else if(actionCounter==3) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], ct.turnAction);
				timer.start();
			} 
			parent.repaint();
		}
	};
	ActionListener varostVeszAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			actionCounter++;
			if(actionCounter==1) {
				pl.deck.select("bbkkk");
			} else if(actionCounter==2) {
				pl.varos.mouseClicked(null);
				turnCounter=8-1; // vissza attenni, amit vett
			} else if(actionCounter==3) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], ct.turnAction);
				timer.start();
			} 
			parent.repaint();
		}
	};
	ActionListener hazatVeszAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			actionCounter++;
			if(actionCounter==1) {
				pl.deck.select("ftbj");
			} else if(actionCounter==2) {
				pl.haz.mouseClicked(null);
				turnCounter=7-1; // vissza attenni, amit vett
			} else if(actionCounter==3) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], ct.turnAction);
				timer.start();
			} 
			parent.repaint();
		}
	};
	ActionListener utatVeszAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			actionCounter++;
			if(actionCounter==1) {
				pl.deck.select("ft");
			} else if(actionCounter==2) {
				pl.ut.mouseClicked(null);
				turnCounter=6-1; // vissza attenni, amit vett
			} else if(actionCounter==3) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], ct.turnAction);
				timer.start();
			} 
			parent.repaint();
		}
	};
	ActionListener varosEpitAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			actionCounter++;
			if(actionCounter==1) {
				pl.varos.mouseClicked(null);
			} else if(actionCounter==2) {
				Junction junc=AI.getBestVaros(currentPlayer);
				if(junc!=null) junc.haz.mouseClicked(null); else pl.varos.mouseClicked(null);
			} else if(actionCounter==3) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], ct.turnAction);
				timer.start();
			} 
			parent.repaint();
		}
	};
	ActionListener hazEpitAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			actionCounter++;
			if(actionCounter==1) {
				pl.haz.mouseClicked(null);
			} else if(actionCounter==2) {
				Junction junc=AI.getBestHaz(currentPlayer,returnTurnAction==firstTwoRoundsTurnAction);
				if(junc!=null) junc.haz.mouseClicked(null); else pl.haz.mouseClicked(null);
				if(returnTurnAction==firstTwoRoundsTurnAction) firstTwoHaz=junc;
			} else if(actionCounter==3) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], returnTurnAction);
				timer.start();
			} 
			parent.repaint();
		}
	};
	ActionListener utEpitAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			Junction[] juncs = new Junction[2];
			actionCounter++;
			if(actionCounter==1) {
				if(!pl.ut2.invisible)
					pl.ut2.mouseClicked(null);
				else
					pl.ut.mouseClicked(null);
			} else if(actionCounter==2) {
				if(returnTurnAction==ct.turnAction)
					juncs=AI.getBestRoad(currentPlayer,null);
				else
					juncs=AI.getBestRoad(currentPlayer,firstTwoHaz);
				if(juncs==null) juncs=AI.getLongestRoad(currentPlayer);
				if(juncs!=null) parent.mouseClicked(new MouseEvent(parent,0,0,0,
					(juncs[1].x+juncs[2].x)/2,(juncs[1].y+juncs[2].y)/2,1,false,MouseEvent.BUTTON1));
				else if(pl.ut2.invisible) pl.ut.mouseClicked(null); else pl.ut2.mouseClicked(null);
			} else if(actionCounter==3) {
				if(returnTurnAction==ct.turnAction)
					turnCounter=6-1; // vissza, ha van megegy ut attenni
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], returnTurnAction);
				timer.start();
			} 
			parent.repaint();
		}
	};
	ActionListener lapKellAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			timer.stop();
			cardToPlayOut=AI.searchFacedownCard('f',currentPlayer);
			if(cardToPlayOut==null) cardToPlayOut=AI.searchFacedownCard('m',currentPlayer);
			if(cardToPlayOut!=null) {
				timer = new javax.swing.Timer(nagyTime[S.animSpeed], felfedezesMonopolAction);
				timer.start();
			} else {
				timer = new javax.swing.Timer(nagyTime[S.animSpeed], becserelAction);
				timer.start();
			}
		}
	};
	ActionListener becserelAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			actionCounter++;
			char[] csereCards = cs.getCsereCards();
			if(csereCards==null) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], ct.turnAction);
				timer.start();
			}
			else if(actionCounter==1) {
				for(int i=0;i<csereCards.length;i++) 
					pl.deck.getDeselectRes(csereCards[i]).selected=true;
				pl.deck.alignCards();
				parent.repaint();
			} else if(actionCounter==2) {
				pl.deck.removeSelect();
				cs.removeCsereCards(csereCards);
				pl.deck.addCard('R',cs.missing[0]);
				parent.repaint();
				csinaltValamit=true;
				if(cs.missing[1]==' ') {
					actionCounter+=2; // atugorjuk, ha csak egyet kell
					turnCounter--; // vissza elkolteni, amire becserelt
				}
			} else if(actionCounter==3) {
				for(int i=0;i<csereCards.length;i++) 
					pl.deck.getDeselectRes(csereCards[i]).selected=true;
				pl.deck.alignCards();
				parent.repaint();
			} else if(actionCounter==4) {
				pl.deck.removeSelect();
				pl.deck.addCard('R',cs.missing[1]);
				csinaltValamit=true;
				turnCounter--; // vissza elkolteni, amire t
				parent.repaint();
			} else if(actionCounter==5) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], ct.turnAction);
				timer.start();
			}
		}
	};
	ActionListener felfedezesMonopolAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			actionCounter++;
			if(actionCounter==1) {
				pl.eDeck.eloreCard(cardToPlayOut); parent.repaint();
			} else if(actionCounter==2) {
				if(cardToPlayOut.res=='f') { // felfedezes
					cardToPlayOut.adjEgyResourcet(cs.missing[0]);
					if(cs.missing[1]==' ') cardToPlayOut.adjEgyResourcet(cs.missing[0]);
					else cardToPlayOut.adjEgyResourcet(cs.missing[1]);
				} else if(cardToPlayOut.res=='m') { // monopol
//					U.bang("Monopol:"+new Character(cs.missing[0]).toString()+".");
					cardToPlayOut.addOsszesResourcet(cs.missing[0]);
				}
				cs.mikell("",' ',' ');
				pl.eDeck.removeFaceup(cardToPlayOut.res);
				parent.pakli.teddvissza(cardToPlayOut.res);
				parent.repaint();
				csinaltValamit=true;
			} else if(actionCounter==3) {
				timer.stop();
				timer = new javax.swing.Timer(kisTime[S.animSpeed], ct.turnAction);
				timer.start();
			}
		}
	};
}
