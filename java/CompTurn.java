import java.awt.event.*;

public class CompTurn {
	
	S parent;
	CompAction ca;

	public CompTurn(S pParent) {
		parent=pParent;
		ca = new CompAction(parent,this);
	}

	public void turn(int pCurrentPlayer) {
		parent.repaint();
		ca.currentPlayer=pCurrentPlayer;
		ca.pl = parent.p[ca.currentPlayer];
		if(S.noRounds<=2) {
			ca.turnCounter = 0;
			ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], ca.firstTwoRoundsTurnAction);
			ca.timer.start();
		} else {
			ca.turnCounter = 0;
			ca.csinaltValamit=false;
			ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
			ca.timer.start();
		}
	}
		
	ActionListener turnAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			ca.timer.stop();
			ca.turnCounter++;
			if(ca.turnCounter==1) {
				// kocka dobas
				ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.kockaAction);
				ca.timer.start();
			} else if(ca.turnCounter==2) {
				// ha hetes volt, es a Rablo aktiv, replace rablo
				if(parent.rablo.active) {
					ca.actionCounter = 0;
					ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.rabloReplaceAction);
					ca.timer.start();
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==3) {
				// ha van kijatszhato Knight, es a rablo rajtunk all, vagy tobb is van, kijatssza
				ca.cardToPlayOut = AI.searchFacedownCard('l',ca.currentPlayer);
				Card masikKnight=null;
				if(ca.cardToPlayOut!=null) {
					ca.cardToPlayOut.showCard();
					masikKnight = AI.searchFacedownCard('l',ca.currentPlayer);
					ca.cardToPlayOut.hideCard();
				}
				if((ca.cardToPlayOut!=null)
				&&((AI.rabloHurts(ca.currentPlayer))||(masikKnight!=null))) {
					ca.pl.eDeck.eloreCard(ca.cardToPlayOut); parent.repaint();
					parent.rablo.active=true;
					parent.longest.setLongestRoad();
					ca.actionCounter = 0;
					ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.rabloReplaceAction);
					ca.timer.start();
					ca.csinaltValamit=true;
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==4) {
				// ha van kijatszhato Siegpunkt, rogton kijatssza
				ca.cardToPlayOut = AI.searchSiegpunktCard(ca.currentPlayer);
				if(ca.cardToPlayOut!=null) {
					ca.pl.eDeck.eloreCard(ca.cardToPlayOut);
					ca.pl.siegpunkt++;
					parent.repaint();
					ca.csinaltValamit=true;
					ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], turnAction);
					ca.timer.start();
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==5) {
				// Strassenbau kijatszasa
				ca.cardToPlayOut = AI.searchFacedownCard('u',ca.currentPlayer);
				if(((AI.getBestHaz(ca.currentPlayer,false)==null) // nincs hova hazat rakni
					||(ca.pl.noHazVaros[1]>=Player.maxNoHazVaros[1])) // vagy nincs mar tobb haz
				&&(ca.cardToPlayOut!=null)
				&&(ca.pl.ut.invisible)&&(ca.pl.ut2.invisible)
				&&(ca.pl.noUt<Player.maxNoUt)
				&&(AI.getLongestRoad(ca.currentPlayer)!=null)) { // tud meg utat rakni barhova
					ca.actionCounter=0;
					ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.strassenbauAction);
					ca.timer.start();
					ca.csinaltValamit=true;
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==6) {
				// ha van ut, es fer meg a tablara, atteszi a tablara
				if(((!ca.pl.ut.invisible)||(!ca.pl.ut2.invisible))
				&&(AI.getLongestRoad(ca.currentPlayer)!=null)) { // fer meg utja a tablara
					ca.actionCounter = 0; ca.returnTurnAction=turnAction;
					ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.utEpitAction);
					ca.timer.start();
					ca.csinaltValamit=true;
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==7) {
				// ha van haz, es van hova tenni, atteszi a tablara
				if((ca.pl.haz.type==1)
				&&(AI.getBestHaz(ca.currentPlayer,false)!=null)) {
					ca.actionCounter = 0; ca.returnTurnAction=turnAction;
					ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.hazEpitAction);
					ca.timer.start();
					ca.csinaltValamit=true;
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==8) {
				// ha van varos, es van hova tenni, atteszi a tablara
				if((ca.pl.varos.type==2)
				&&(AI.getBestVaros(ca.currentPlayer)!=null)) {
					ca.actionCounter = 0;
					ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.varosEpitAction);
					ca.timer.start();
					ca.csinaltValamit=true;
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==9) {
				// ha kell ut, es van "ft", vesz egyet, kulonben probal cserelni
				ca.cs.mikell("",' ',' ');
				if(
				(ca.pl.ut.invisible)									// nincs ut az arakKartya-n
				&&(ca.pl.noUt<Player.maxNoUt) 							// van meg ut
				&&(((AI.getBestHaz(ca.currentPlayer,false)==null)		// nincs hova hazat
				  &&(AI.getBestRoad(ca.currentPlayer,null)!=null)		// van ut hazhelyhez
				  &&(ca.pl.noHazVaros[1]<Player.maxNoHazVaros[1])) 		// van meg haz
				  ||((ca.pl.noHazVaros[1]>=Player.maxNoHazVaros[1])		// vagy nincs tobb haz
					||(AI.getBestRoad(ca.currentPlayer,null)==null))	// vagy nincs ut hazhelyhez
					&&((AI.getBestVaros(ca.currentPlayer)==null))		// nincs hova varost
					||(ca.pl.noHazVaros[2]>=Player.maxNoHazVaros[2]))	// nincs mar varos
				) { 
					if(ca.pl.deck.isMegvanBenne("ft")) {
						ca.actionCounter=0;
						ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.utatVeszAction);
						ca.timer.start();
						ca.csinaltValamit=true;
					} else if((AI.getBestVaros(ca.currentPlayer)==null) // nincs hely varosnak
					||(ca.pl.noHazVaros[2]>=Player.maxNoHazVaros[2]) // nincs tobb varosunk
					||((!ca.pl.deck.isMegvanBenne("bkkk"))&&(!ca.pl.deck.isMegvanBenne("bbkk")))) {
						if(ca.pl.deck.isMegvanBenne("f")) ca.cs.mikell("f",'t',' ');
						else if(ca.pl.deck.isMegvanBenne("t")) ca.cs.mikell("t",'f',' ');
						else ca.cs.mikell("",'f','t');
						if(ca.cs.missing[0]!=' ') {
							ca.actionCounter=0;
							ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], ca.lapKellAction);
							ca.timer.start();
						} else {
							ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
							ca.timer.start();
						}
					} else {
						ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
						ca.timer.start();
					}
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==10) {
				// ha lehet hazat rakni, es van "ftbj", vesz egyet, kulonben probal cserelni
				ca.cs.mikell("",' ',' ');
				if((AI.getBestHaz(ca.currentPlayer,false)!=null)
				&&(ca.pl.haz.type==0)
				&&(ca.pl.noHazVaros[1]<Player.maxNoHazVaros[1])) {
					if(ca.pl.deck.isMegvanBenne("ftbj")) {
						ca.actionCounter=0;
						ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.hazatVeszAction);
						ca.timer.start();
						ca.csinaltValamit=true;
					} else if((AI.getBestVaros(ca.currentPlayer)==null) // nincs hely varosnak
					||(ca.pl.noHazVaros[2]>=Player.maxNoHazVaros[2]) // nincs tobb varosunk
					||((!ca.pl.deck.isMegvanBenne("bkkk"))&&(!ca.pl.deck.isMegvanBenne("bbkk")))) {
						if(ca.pl.deck.isMegvanBenne("ftb")) ca.cs.mikell("ftb",'j',' ');
						else if(ca.pl.deck.isMegvanBenne("ftj")) ca.cs.mikell("ftj",'b',' ');
						else if(ca.pl.deck.isMegvanBenne("fbj")) ca.cs.mikell("fbj",'t',' ');
						else if(ca.pl.deck.isMegvanBenne("tbj")) ca.cs.mikell("tbj",'f',' ');
						else if(ca.pl.deck.isMegvanBenne("ft")) ca.cs.mikell("ft",'b','j');
						else if(ca.pl.deck.isMegvanBenne("fb")) ca.cs.mikell("fb",'t','j');
						else if(ca.pl.deck.isMegvanBenne("fj")) ca.cs.mikell("fj",'t','b');
						else if(ca.pl.deck.isMegvanBenne("tb")) ca.cs.mikell("tb",'f','j');
						else if(ca.pl.deck.isMegvanBenne("tj")) ca.cs.mikell("tj",'f','b');
						else if(ca.pl.deck.isMegvanBenne("bj")) ca.cs.mikell("bj",'f','t');
						if(ca.cs.missing[0]!=' ') {
							ca.actionCounter=0;
							ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], ca.lapKellAction);
							ca.timer.start();
						} else {
							ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
							ca.timer.start();
						}
					} else {
						ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
						ca.timer.start();
					}
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==11) {
				// ha lehet varost rakni, es van "bbkkk", akkor vesz egyet, kulonben pr.cs.
				ca.cs.mikell("",' ',' ');
				if((AI.getBestVaros(ca.currentPlayer)!=null)
				&&(ca.pl.varos.type==0)
				&&(ca.pl.noHazVaros[2]<Player.maxNoHazVaros[2])) {
					if(ca.pl.deck.isMegvanBenne("bbkkk")) {
						ca.actionCounter=0;
						ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.varostVeszAction);
						ca.timer.start();
						ca.csinaltValamit=true;
					} else if(ca.cs.missing[0]==' ') {
						if(ca.pl.deck.isMegvanBenne("bbkk")) ca.cs.mikell("bbkk",'k',' ');
						else if(ca.pl.deck.isMegvanBenne("bkkk")) ca.cs.mikell("bkkk",'b',' ');
						else if(ca.pl.deck.isMegvanBenne("kkk")) ca.cs.mikell("kkk",'b','b');
						else if(ca.pl.deck.isMegvanBenne("bkk")) ca.cs.mikell("bkk",'b','k');
						else if(ca.pl.deck.isMegvanBenne("bbk")) ca.cs.mikell("bbk",'k','k');
						if(ca.cs.missing[0]!=' ') {
							ca.actionCounter=0;
							ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], ca.lapKellAction);
							ca.timer.start();
						} else {
							ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
							ca.timer.start();
						}
					} else {
						ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
						ca.timer.start();
					}
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==12) {
				// ha van meg a pakliban, es nem egy hianyzik mashoz, akkor lapot vesz-cserel
				ca.cs.mikell("",' ',' ');
				if(
				(parent.pakli.deck.noCards()>0)
				
				&&(
				((AI.getBestVaros(ca.currentPlayer)==null) // nincs hely varosnak
				||(ca.pl.noHazVaros[2]>=Player.maxNoHazVaros[2]) // nincs tobb varosunk
				||((!ca.pl.deck.isMegvanBenne("bkkk"))&&(!ca.pl.deck.isMegvanBenne("bbkk"))))
				
				&&(
				(AI.getBestHaz(ca.currentPlayer,false)==null) // nincs hely haznak
				||(ca.pl.noHazVaros[1]>=Player.maxNoHazVaros[1]) // nincs tobb hazunk
				||((!ca.pl.deck.isMegvanBenne("ftb"))&&(!ca.pl.deck.isMegvanBenne("ftj"))
				  &&(!ca.pl.deck.isMegvanBenne("fbj"))&&(!ca.pl.deck.isMegvanBenne("tbj")))))) {
				  	
					if(ca.pl.deck.isMegvanBenne("bjk")) {
						ca.actionCounter=0;
						ca.timer = new javax.swing.Timer(ca.nagyTime[S.animSpeed], ca.lapotVeszAction);
						ca.timer.start();
						ca.csinaltValamit=true;
					// ha egy hianyzik, es nem ketto hianyzik mashoz, akkor lapot cserel
					} else if(((AI.getBestVaros(ca.currentPlayer)==null) // nincs hely varosnak
					||(ca.pl.noHazVaros[2]>=Player.maxNoHazVaros[2]) // nincs tobb varosunk
					||((!ca.pl.deck.isMegvanBenne("kkk"))&&(!ca.pl.deck.isMegvanBenne("bkk"))
						&&(!ca.pl.deck.isMegvanBenne("bbk"))))
					&&((AI.getBestHaz(ca.currentPlayer,false)==null) // nincs hely haznak
					||(ca.pl.noHazVaros[1]>=Player.maxNoHazVaros[1]) // nincs tobb hazunk
					||((!ca.pl.deck.isMegvanBenne("ft"))&&(!ca.pl.deck.isMegvanBenne("fb"))
					&&(!ca.pl.deck.isMegvanBenne("fj"))&&(!ca.pl.deck.isMegvanBenne("tb"))
					&&(!ca.pl.deck.isMegvanBenne("tj"))&&(!ca.pl.deck.isMegvanBenne("bj"))))){
						if(ca.pl.deck.isMegvanBenne("bj")) ca.cs.mikell("bj",'k',' ');
						else if(ca.pl.deck.isMegvanBenne("bk")) ca.cs.mikell("bk",'j',' ');
						else if(ca.pl.deck.isMegvanBenne("jk")) ca.cs.mikell("jk",'b',' ');
						if(ca.cs.missing[0]!=' ') {
							ca.actionCounter=0;
							ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], ca.lapKellAction);
							ca.timer.start();
						} else {
							ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
							ca.timer.start();
						}
					} else {
						ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
						ca.timer.start();
					}
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==13) {
				// ha tul sok kartyaja van, es nem egy hianyzik mashoz, cserel olyanra, ami nincs
				if((ca.pl.deck.noCards()>8)
					&&(
					(parent.pakli.deck.noCards()==0)		// nincs mar a pakliban
					||(!ca.pl.deck.isMegvanBenne("bj"))&&(!ca.pl.deck.isMegvanBenne("bk"))
					  &&(!ca.pl.deck.isMegvanBenne("jk")))
					
					&&(
					((AI.getBestVaros(ca.currentPlayer)==null) // nincs hely varosnak
					||(ca.pl.noHazVaros[2]>=Player.maxNoHazVaros[2]) // nincs tobb varosunk
					||((!ca.pl.deck.isMegvanBenne("bkkk"))&&(!ca.pl.deck.isMegvanBenne("bbkk"))))
				
					&&(
					(AI.getBestHaz(ca.currentPlayer,false)==null) // nincs hely haznak
					||(ca.pl.noHazVaros[1]>=Player.maxNoHazVaros[1]) // nincs tobb hazunk
					||((!ca.pl.deck.isMegvanBenne("ftb"))&&(!ca.pl.deck.isMegvanBenne("ftj"))
					  &&(!ca.pl.deck.isMegvanBenne("fbj"))&&(!ca.pl.deck.isMegvanBenne("tbj")))))) {

					if(!ca.pl.deck.isMegvanBenne("b")) ca.cs.mikell("",'b',' ');
					else if(!ca.pl.deck.isMegvanBenne("kk")) {
						if(ca.pl.deck.isMegvanBenne("k")) ca.cs.mikell("k",'k',' ');
						else ca.cs.mikell("",'k',' ');
					}
					else if(!ca.pl.deck.isMegvanBenne("f")) ca.cs.mikell("",'f',' ');
					else if(!ca.pl.deck.isMegvanBenne("t")) ca.cs.mikell("",'t',' ');
					else if(!ca.pl.deck.isMegvanBenne("j")) ca.cs.mikell("",'j',' ');
					ca.actionCounter=0;
					if(ca.cs.missing[0]!=' ') {
						ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], ca.lapKellAction);
						ca.csinaltValamit=true;
					} else
						ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				} else {
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
			} else if(ca.turnCounter==14) {
				if(ca.csinaltValamit) {
					ca.csinaltValamit=false;
					ca.turnCounter=2;
					ca.timer = new javax.swing.Timer(ca.kisTime[S.animSpeed], turnAction);
					ca.timer.start();
				}
				else if(parent.ps.nextPlayer().comp) turn(parent.ps.current);
				else parent.getButton("kovetkezo").setEnabled(true);
			}
		}
	};
}
