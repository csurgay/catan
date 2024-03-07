import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Player extends JComponent {
	S parent;
	static int sx=900; // size
	static int sy=320;
	static int dx=5; // offset for arakKartya
	static int dy=10; 
	static int rx=30; // roundRect
	static int ry=15;
	static int bb=3; // border

	int player; // player number or color
	boolean comp; // if computer player
	int siegpunkt;
	Haz haz, varos;
	UtClass ut,ut2;
	boolean vanUt2;
	EntwClass ew;
	JLabel utLabel, hadseregLabel; 
	public Deck deck; // Resource deck
	public Deck eDeck; // Entwicklungs deck
	
	static int[] maxNoHazVaros = new int[] {0, 5,4};
	static int maxNoUt = 15;
	int[] noHazVaros = new int[3];
	int noUt;
	
	public int hanyadikHaza;

	public Player(int pPlayer, S pParent) {
		player=pPlayer;
		parent=pParent;
		comp=false;
		setSize(sx,sy);
//		setBorder(new javax.swing.border.BevelBorder(1));

		deck=new Deck('R',parent); deck.setBounds(dx,210,sx-dx,111); add(deck);

		eDeck=new Deck('E',parent); eDeck.setBounds(dx+200,dy,sx-dx-200,216); add(eDeck);

		ut=new UtClass(parent,'1'); add(ut); ut.setBounds(dx+150,dy+46,34,10);
		ut2=new UtClass(parent,'2'); add(ut2); vanUt2=true; ut2.setBounds(dx+150,dy+36,34,10);
//		remove(ut2); vanUt2=false;
		ew=new EntwClass(parent); add(ew); ew.setBounds(dx+150,dy+170,34,34);
//		ew.setBorder(new javax.swing.border.BevelBorder(1));
		
		haz=new Haz(1,player,true,parent); add(haz); haz.setLocation(dx+159,dy+92);
		haz.constrainType=1; haz.playerPointer=this;
		varos=new Haz(2,player,true,parent); add(varos); varos.setLocation(dx+151,dy+137);
		varos.constrainType=2; varos.playerPointer=this;
		JLabel arakLabel=new JLabel(ImageLoader.kartya[0]); add(arakLabel); 
		arakLabel.setBounds(dx,dy,190,216);

		utLabel=new JLabel(ImageLoader.kartya[1]); add(utLabel); 
		utLabel.setBounds(210+60,dy+107,95,108); utLabel.setVisible(false);
		hadseregLabel=new JLabel(ImageLoader.kartya[2]); add(hadseregLabel); 
		hadseregLabel.setBounds(210+60,dy,95,108); hadseregLabel.setVisible(false);
	}
	
	public void init() {
		siegpunkt=0;
//		if(vanUt2) remove(ut2);
		ut.invisible=true;
		ut2.invisible=true;
		haz.setVisible(true); haz.type=0;
		varos.setVisible(true); varos.type=0;
		deck.removeAll(); eDeck.removeAll();
		noHazVaros[1]=0; noUt=0; noHazVaros[2]=0;
		hanyadikHaza=0;
	}

	// utacska az arakKartya-n
	protected class UtClass extends JComponent implements MouseListener {
	
		S parent;
		char ut12;
		boolean invisible;
		
		public UtClass(S pParent,char pUt12) {
			parent=pParent;
			ut12=pUt12;
			invisible=false;
			addMouseListener(this);
		}
	
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {
			// invisible es nincs mozgo -> venni akar
			if ((!S.utMoves)&&(invisible)&&(ut12!='2')&&(!comp)) {
				String utS = new String("ft");			// ezt talan setVisible(false)sal kellene
				deck.saveSelect();
				if (!deck.isSelect(utS)) {
					if(deck.isMegvanBenne(utS)) {
						deck.select(utS);
						parent.repaint();
					}
				}
			}
		}
		public void mouseExited(MouseEvent e) {
			// invisible es nincs mozgo -> venni akart
			if ((!S.utMoves)&&(invisible)&&(ut12!='2')&&(!comp)) {
				deck.restoreSelect();		// ezt talan setVisible(false)sal kellene
				parent.repaint();
			}
		}
		public void mouseClicked(MouseEvent e) {
			
			// visible, nincs mozgo, es rakattintotak -> el akarjak vinni
			if((!S.utMoves)&&(!invisible)) {
				S.utMoves=true; S.oldCursor=getCursor(); parent.setCursor(S.utCursor);
				S.movedUt=player;
				invisible=true;
				repaint();
				
			// invisible es egy ilyen szinu mozgo uttal rakattintottak -> visszavaltas
			} else if((S.utMoves)&&(invisible)&&(S.movedUt==player)){
				S.utMoves=false; parent.setCursor(S.oldCursor);
//				inactive=false;
				deck.addCard('R','f'); deck.addCard('R','t');
				noUt--;
				repaint();
				
			// invisible es nincs mozgo -> most vesz egyet
			} else if ((!S.utMoves)&&(invisible)) {
				if(noUt<maxNoUt) {
					if (deck.isSelect(new String("ft"))) {
						invisible=false;
						deck.removeSelect();
						noUt++;
						parent.repaint();
					}
				} else  {
					U.bang("Cannot build more than "+new Integer(maxNoUt).toString()+" roads");
				}
			}
		}

		public void paintComponent(Graphics gg) {
			
			Graphics2D g = (Graphics2D)gg.create();
			g.setStroke(new BasicStroke(6.0f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
	
			g.setColor(U.playerColor[player]);
			if(!invisible) g.drawLine(0,3,32,3);
			
			g.dispose();
		}
	}
	
	// Entwicklungs kartya hely az arakKartya-n
	protected class EntwClass extends JComponent implements MouseListener {
	
		S parent;
		
		public EntwClass(S pParent) {
			parent=pParent;
			addMouseListener(this);
		}
	
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {
			String utS = new String("bjk");
			deck.saveSelect();
			if (!deck.isSelect(utS)) {
				if(deck.isMegvanBenne(utS)) {
					deck.select(utS);
					parent.repaint();
				}
			}
		}
		public void mouseExited(MouseEvent e) {
			deck.restoreSelect();
			parent.repaint();
		}
		public void mouseClicked(MouseEvent e) {
			String utS = new String("bjk");
			if(deck.isSelect(utS)) {
				deck.removeSelect();
				eDeck.addCard('E',parent.pakli.huzzegyet());
				parent.repaint();
			}
		}
	}
}
