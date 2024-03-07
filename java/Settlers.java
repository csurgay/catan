//import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class Settlers extends JApplet implements ActionListener, Serializable {

	S s;
	JMenuItem menuAnimSpeed, menuPakli, menuInternals, 
		menuHanyadik, menuOpenHands, menuPlayerAll, menuPlayerNone, menuWhoBegins;
	JCheckBoxMenuItem[] menuPlayer;
	JRadioButtonMenuItem[] menuBoard;
	JMenuItem[] menuBeginner;
	
	public void createGUI() {
		
//		Csak JFrame eseten
//		setTitle("Settlers");
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(null);
		final int rightx = 1024;
		final int bottomy = 768;
		final int s_dx = 550;
		final int s_dy = 630;
		setBounds(0, 0, rightx, bottomy);
		
		s = new S(this);
		
		s.setBounds(0,0,rightx,bottomy);
		getContentPane().add(s);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuBoards = new JMenu("New"); // Board menu
		menuBar.add(menuBoards);
		
		menuBoard = new JRadioButtonMenuItem[4];
		
		for(int i=0;i<4;i++) {
			menuBoard[i] = new JRadioButtonMenuItem("Game for "+new Integer(i+3).toString()+" players");
			menuBoards.add(menuBoard[i]);
			menuBoard[i].addActionListener(this);
		}
		menuBoard[3].setSelected(true);

		JMenu menuPlayers = new JMenu("Players"); // Players menu
		menuBar.add(menuPlayers);

		menuWhoBegins = new JMenuItem(" will begin", 
			ImageLoader.playerIcon[S.beginnerPlayer]); // ez ilyenkor 1
		menuPlayers.add(menuWhoBegins);
		
		menuPlayers.add(new JSeparator());
		
		JMenu menuBeginners = new JMenu("Player to begin");
		menuPlayers.add(menuBeginners);
		
		menuBeginner = new JMenuItem[7];
		for(int i=1;i<=6;i++) {
			menuBeginner[i] = new JMenuItem(" will begin", 
				ImageLoader.playerIcon[i]);
			menuBeginners.add(menuBeginner[i]);
			menuBeginner[i].addActionListener(this);
		}

		JMenu menuComputer = new JMenu("Computer"); // Computer menu
		menuBar.add(menuComputer);

		menuPlayerAll = new JMenuItem( "All Players Computer");
		menuComputer.add(menuPlayerAll);
		menuPlayerAll.addActionListener(this);

		menuPlayerNone = new JMenuItem( "All Players Human");
		menuComputer.add(menuPlayerNone);
		menuPlayerNone.addActionListener(this);
		menuComputer.add(new JSeparator());

		menuPlayer = new JCheckBoxMenuItem[7];
		for(int i=1;i<=6;i++) {
			menuPlayer[i] = new JCheckBoxMenuItem( " Computer", 
				ImageLoader.playerIcon[i]);
			menuComputer.add(menuPlayer[i]);
			menuPlayer[i].addActionListener(this);
		}

		JMenu menuOptions = new JMenu("Options"); // Options menu
		menuBar.add(menuOptions);

		menuAnimSpeed = new JCheckBoxMenuItem("Fast Animation");
		menuOptions.add(menuAnimSpeed);
		menuAnimSpeed.addActionListener(this);

		menuPakli = new JCheckBoxMenuItem("Entwicklungs Deck");
		menuOptions.add(menuPakli);
		menuPakli.addActionListener(this);

		menuInternals = new JCheckBoxMenuItem("Show Internals");
		menuOptions.add(menuInternals);
		menuInternals.addActionListener(this);

		menuHanyadik = new JCheckBoxMenuItem("Deployment Order");
		menuOptions.add(menuHanyadik);
		menuHanyadik.addActionListener(this);
		
		menuOpenHands = new JCheckBoxMenuItem("Open Hands");
		menuOptions.add(menuOpenHands);
		menuOpenHands.addActionListener(this);

		setVisible(true);
		repaint();
	}
	
	
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)(e.getSource());
		if(source==menuAnimSpeed) {
//			try {
//				FileOutputStream out = new FileOutputStream("S.save");
//				ObjectOutputStream s = new ObjectOutputStream(out);
//				s.writeObject("Today");
//				s.writeObject(s);
//				s.flush();
//			} catch (FileNotFoundException ex){
//			} catch (IOException ex) {
//			}
			S.animSpeed = 1 - S.animSpeed;

		} else if(source==menuPakli) {
			if(!s.isAncestorOf(s.pakli)) s.add(s.pakli,0); else s.remove(s.pakli);
			repaint();
		} else if(source==menuInternals) {
			S.internals=!S.internals;
			repaint();
		} else if(source==menuHanyadik) {
			S.hanyadik=!S.hanyadik;
			repaint();
		} else if(source==menuOpenHands) {
			S.openHands=!S.openHands;
			repaint();
		} else if(source==menuPlayerAll) {
			for(int i=1;i<=6;i++) {
				s.p[i].comp = true;
				menuPlayer[i].setState(true);
			} 
			repaint();
		} else if(source==menuPlayerNone) {
			for(int i=1;i<=6;i++) {
				s.p[i].comp = false;
				menuPlayer[i].setState(false);
			} 
			repaint();
		} 
		for(int i=0;i<4;i++) if(source==menuBoard[i]) {
			for(int j=0;j<4;j++) menuBoard[j].setSelected(false); menuBoard[i].setSelected(true);
			if((i==0)||(i==1)) s.b.meret=34; else s.b.meret=56; S.noPlayer=i+3; s.newGame();
			for(int j=1;j<=6;j++) {
				menuBeginner[j].setEnabled(false);
				menuPlayer[j].setEnabled(false);
			}
			for(int j=1;j<=i+3;j++) {
				menuBeginner[j].setEnabled(true);
				menuPlayer[j].setEnabled(true);
			}
			if(S.beginnerPlayer>i+3) S.beginnerPlayer=1;
			menuWhoBegins.setIcon(ImageLoader.playerIcon[S.beginnerPlayer]);
			repaint();
		} 
		for(int i=1;i<=6;i++) { 
			if(source==menuPlayer[i]) {
				s.p[i].comp = !s.p[i].comp; 
				repaint();
			}
		}
		for(int i=1;i<=6;i++) { 
			if(source==menuBeginner[i]) {
				S.beginnerPlayer = i;
				menuWhoBegins.setIcon(ImageLoader.playerIcon[S.beginnerPlayer]);
				repaint();
			}
		}
	}
	
//	public static void main(String args[]) {
//		JFrame.setDefaultLookAndFeelDecorated(true);
//		JDialog.setDefaultLookAndFeelDecorated(true);
//		Settlers settlers = new Settlers();
//		settlers.createGUI();
//	}

	public ImageIcon getIcon(String filename) {

		String fn = "image/".concat(filename);

//		For JApplet!
//		try {return new ImageIcon(new URL(this.getCodeBase(), fn));} catch (java.net.MalformedURLException e) {U.o("getIcon!");return null;}
//		For JFrame
		Class c = getClass();
		URL res = c.getResource(fn);
		if(res!=null) {
			U.o(res.toString());
			return new ImageIcon(res);} 
		else {
			U.o("null: "+fn);
			return null;
		} 

	}

	private final class EDThread implements Runnable {
		public void run() {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			createGUI();
		}
	}
	
	public void init() {
		U.o("init");
		U.o("Settlers version 2004.04.10");	// Just a report to Java console
		
		//Execute a job on the event-dispatching thread:
		//creating this applet's GUI.
		try {
			javax.swing.SwingUtilities.invokeAndWait(new EDThread());
		} catch (Exception e) {
			System.err.println("createGUI didn't successfully complete");
		}
	}

	public void start() {
		U.o("start");
	}
}

