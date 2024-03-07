import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class S extends JComponent implements MouseListener, ActionListener {

	Settlers frame;
	static Random R;
	static boolean internals;
	ImageLoader imageLoader;
	Board b;
	Pakli pakli;
	Kocka kocka;
	Rablo rablo;
	Player[] p;
	PlayerSelect ps;
	static int noPlayer; static int beginnerPlayer;
	static int noRounds;
	Longest longest;
	Object[] resIcons;
	CompTurn comp;
	static boolean aiSuspend;
	static int animSpeed=1;
	static boolean arrange=false;
	static boolean hanyadik=false;
	static boolean openHands=false;
	static Cursor utCursor;
	
	String[] buttonString = new String[] 
		{"becserel","csere","kovetkezo"};
	JButton[] button = new JButton[buttonString.length];
	JButton butt;
	
	JLabel utLabel, hadseregLabel;
	
	static int dx=10; static int dy=230;
	
	public S(Settlers pSettlers) {
		frame=pSettlers;
		setLayout(null);
		addMouseListener(this);
//		setOpaque(true);
		R = new Random();
		imageLoader = new ImageLoader(pSettlers); // JApplet vagy JFrame
		Haz.init(pSettlers);
		utCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			pSettlers.getIcon("utCursor.gif").getImage(),new Point(15,3),"ut");
		resIcons = new Object[] {ImageLoader.res[1],ImageLoader.res[2], 
			ImageLoader.res[3],ImageLoader.res[4],ImageLoader.res[5]};

		noPlayer=6; beginnerPlayer=1;
		
		Internals inter = new Internals(this); add(inter);

		for(int i=0;i<button.length;i++) {
			button[i]=new JButton(ImageLoader.button[i]);
			add(button[i]); button[i].setBounds(i*50+120,dy,46,60);
			button[i].addActionListener(this); button[i].setActionCommand(buttonString[i]);
		}

		ps = new PlayerSelect(this); ps.setLocation(10,180); add(ps);

		p = new Player[noPlayer+1]; for(int i=1;i<=noPlayer;i++) {
			p[i]=new Player(i,this); add(p[i]); p[i].setBackground(U.playerColor[i]);
			p[i].setLocation(5,290); p[i].setVisible(false); if(!S.openHands) p[i].deck.hideCards();
		}
		p[ps.current].setVisible(true);
		p[ps.current].deck.showCards();
		getButton("becserel").setEnabled(false);
		getButton("csere").setEnabled(false);

		rablo = new Rablo(this, pSettlers); add(rablo); 

		b = new Board(this,330,50); 
		b.meret=56;
		b.create();
		b.addHazak(); 
		b.init();

		pakli = new Pakli(this); pakli.setBounds(300,0,400,400);
		
		kocka = new Kocka(this); add(kocka); kocka.setLocation(dx,dy);

		hadseregLabel=new JLabel(ImageLoader.kartya[2]); add(hadseregLabel); 
		utLabel=new JLabel(ImageLoader.kartya[1]);  add(utLabel); 

		longest=new Longest(this); longest.init();

		add(b);
		
		AI.setParent(this);
		comp = new CompTurn(this);
		add(comp.ca.cs,0); comp.ca.cs.setLocation(300,100);
		aiSuspend=false;
	}

	public void newGame() {
		if(comp.ca.timer!=null)
			if(comp.ca.timer.isRunning()) 
				comp.ca.timer.stop();
		for(int i=1;i<=noPlayer;i++) p[i].init();
		longest.init();
		b.removeHazak(); remove(b); b.create(); b.addHazak(); b.init(); add(b);		
		if(b.meret==34) {
			hadseregLabel.setBounds(690,505,95,108); 
			utLabel.setBounds(750,470,95,108); 
		} else if(b.meret==56) {
			hadseregLabel.setBounds(850,545,95,108); 
			utLabel.setBounds(910,510,95,108); 
		}
		ps.init();
		pakli.init(); pakli.shuffle();
		kocka.active=false;
		noRounds=0;
		ps.current--;
		if(ps.nextPlayer().comp) {
//			getButton("kovetkezo").setEnabled(false);
			comp.turn(ps.current);
		}
	}
	
	static boolean utMoves = false; static int movedUt;
	static Cursor oldCursor;
	
	public void actionPerformed(ActionEvent e) {
		String source = e.getActionCommand();

		if(source.equals("becserel")) {
			int selV = JOptionPane.showOptionDialog(null, 
			"Take a resource card", "Trading in",
//			"", "Erfindung",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.PLAIN_MESSAGE, null,
			resIcons,null);
			if(selV!=-1) {
				p[ps.current].deck.addCard('R',"ftbjk".charAt(selV));
				p[ps.current].deck.removeSelect();
			}

		} else if(source.equals("csere")) {
			int selV = JOptionPane.showOptionDialog(null, 
			"Which resource do would you like to get in return?", "Trade Offer",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.PLAIN_MESSAGE, null,
			resIcons, null);
						
			if(selV!=-1) {
				comp.ca.cs.addOffer(ps.current,p[ps.current].deck.getSelected(),"ftbjk".charAt(selV));
				comp.ca.cs.setVisible(true);
			} 

		} else if(source.equals("kovetkezo")) { 
			if(ps.allComp()) {
				if(comp.ca.timer!=null)
					if(comp.ca.timer.isRunning()) 
						comp.ca.timer.stop();
					else comp.ca.timer.start();
			}
			else if(ps.nextPlayer().comp) {
				getButton("kovetkezo").setEnabled(false);
				comp.turn(ps.current);
			}
		}
		repaint();
	}
	
	public JButton getButton(String pS) {
		int k=-1;
		for(int i=0;i<button.length;i++) if(buttonString[i].equals(pS)) k=i;
		return button[k];
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		int x=e.getX(); int y=e.getY();
		if (rablo.moves) {
			Tile tile=b.getNearestTile(x,y,100);
			b.rabloTile=tile;
			tile.hasRablo=true;
			setCursor(rablo.oldcursor); 
			rablo.moves = false;
			rablo.setLocation(tile.x+10,tile.y-25);
			rablo.setVisible(true);
			rablo.robPlayer();
			repaint();
		}
		else if(!Haz.moves) {
			// utra kattintas
			int d=35;
			Junction junc=b.getNearestJunc(x,y,d);
			Junction mJunc=null;
			if(junc!=null) for(int k=0;k<junc.nJuncs;k++) {
				Junction ju = junc.juncs[k];
				int dx=ju.x-x; int dy=ju.y-y;
				if(dx*dx+dy*dy<d*d) mJunc=ju;
			}
			// utra kattintas
			if(mJunc!=null) {
				int ut=junc.getUt(mJunc);
				// felszed utat
				if((!utMoves)&&(ut!=0)&&(arrange)) {
					utMoves=true; oldCursor=getCursor(); setCursor(utCursor);
					movedUt=junc.getUt(mJunc);
					junc.removeUt(mJunc);
					mJunc.removeUt(junc);
				// lerak utat
				} else if((utMoves)&&(ut==0)){
					utMoves=false; setCursor(oldCursor);
					int pl = movedUt;
					junc.addUt(mJunc,pl);
					mJunc.addUt(junc,pl);
				}
				longest.setLongestRoad();
				repaint();
			}
		}
	}

	public void paintComponent(Graphics g) {

		((Graphics2D) g).setRenderingHint(
			 RenderingHints.KEY_ANTIALIASING, 
			 RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D) g).setStroke(new BasicStroke(1.5f, 
							BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
							
//		g.setColor(U.pontertekColor);
		if(U.hatterColor!=null) {
			g.setColor(U.hatterColor);
			g.fillRect(0,0,1000,1000);
		}
	}
	
	public void buttonEnabler() {
		JButton butt;
		butt = getButton("becserel");
		if((butt!=null)&&(p[ps.current]!=null)&&(b!=null)) {
			String sel = p[ps.current].deck.getSelected();
			boolean igen=false;
			if((b.vanKikotoje(ps.current,'3'))&&(sel.length()==3)) igen=true;
			if(sel.length()==2) if((sel.charAt(0)==sel.charAt(1))
				&&(b.vanKikotoje(ps.current,sel.charAt(0)))) igen=true;
			if(sel.length()==4) igen=true;
			if(igen) butt.setEnabled(true); else butt.setEnabled(false);
		}
		butt = getButton("csere");
		if((butt!=null)&&(p[ps.current]!=null)&&(b!=null)) {
			String sel = p[ps.current].deck.getSelected();
			if(sel.length()>0) butt.setEnabled(true); else butt.setEnabled(false);
		}
	}
}