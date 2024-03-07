import javax.swing.*;

public class Pakli extends JComponent {
	
	// 5 pontkartya, 14 lovag, 2 utepito, 2 felfedezes, 2 monopol
	static String cardString = new String("ktepolllllllllllllluuffmm"); // 25 db
	protected int nCards;
	protected char[] cards = new char[40]; // 25 felett random kiosztva
	protected S parent;
	protected Deck deck;
	
	public Pakli(S pParent) {
		parent=pParent;
		
		deck=new Deck('E',parent); deck.setBounds(0,0,400,400); add(deck);
		init();
		shuffle();
	}
	public void init() {
		nCards=25;
		for(int i=0;i<nCards;i++) {
			cards[i]=cardString.charAt(i);
		}
		fillDeck();
	}
	protected void fillDeck() {
		deck.removeAll();
		for(int i=0;i<nCards;i++) deck.addCard('E',cards[i]);
		deck.showCards();
	}
	public void shuffle() {
		for(int i=0;i<nCards;i++) {
			int r=S.R.nextInt(nCards);
			char c=cards[r]; cards[r]=cards[i]; cards[i]=c;
		}
		fillDeck();
	}
	public void teddvissza(char pC) {
		if(nCards>0) {
			int r=S.R.nextInt(nCards);
			for(int i=nCards;i>r;i--) cards[i]=cards[i-1];
			cards[r]=pC;
		} else cards[0]=pC;
		nCards++;
		fillDeck();
	}
	public char huzzegyet() {
		if(nCards<1) {
			if(!parent.ps.allComp()) U.bang("No more cards to take from the deck");
			return ' ';
		} 
		char c=cards[--nCards];
//		for(int i=nCards;i<1;i--) cards[i]=cards[i-1];
		fillDeck();
		return c;
	}
}
