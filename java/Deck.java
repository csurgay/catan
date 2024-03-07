import javax.swing.*;

public class Deck extends JComponent {
	S parent;
	char type; // 'R'-resource deck, 'E'-entwicklungs deck
	Card firstCard, lastCard;
	int dx; static int dxC[] = new int[] {30, 0};
	int dy; static int dyC[] = new int[] { 0,15};
	ImageIcon hatlapIcon;
	int nSelected;
	
	public Deck(char pType, S pParent) {
		parent=pParent; 
		type=pType;
		nSelected=0;
		firstCard=null; lastCard=null;
		if(type=='R') {dx=dxC[0];dy=dyC[0];} 
		if(type=='E') {dx=dxC[1];dy=dyC[1];}
	}

	public void addCard(char pType, char pRes) {
		Card card = new Card(pType,pRes,this,parent); // ausspielen=false
		if(firstCard==null) firstCard=card;
		else lastCard.nextCard=card; 
		lastCard=card;
		if(type=='R') {
			add(card);
			card.showCard(); 
		} else if(type=='E') add(card,0);
		alignCards();
	}
	
	public void eloreCard(Card pCard) {
		removeCard(pCard);
		addCard('E',pCard.res);
		getDeselectRes(pCard.res).showCard();
	}
	
	public void removeCard(Card pCard) {
		Card card = firstCard;
		if(card==pCard) firstCard=card.nextCard;
		else while(card!=null) {
			if(card.nextCard==pCard) {
				card.nextCard=pCard.nextCard;
				if(lastCard==pCard) lastCard=card;
			}
			card=card.nextCard;
		}
		remove(pCard);
		alignCards();
		changedSelectedFaceup();
	}
	
	public int noCards() {
		int n=0;
		Card card = firstCard;
		while(card!=null) {
			n++;
			card=card.nextCard;
		}
		return n;
	}
	
	public void checkCards() {
		Card card = firstCard;
		U.o(type);
		while(card!=null) {
			U.o(card.type,card.res);
			card=card.nextCard;
		}
	}

	public void showCards() {
		Card card = firstCard;
		while(card!=null) {
			card.showCard();
			card=card.nextCard;
		}
		changedSelectedFaceup();
	}

	public void hideCards() {
		Card card = firstCard;
		while(card!=null) {
			card.hideCard();
			card=card.nextCard;
		}
		changedSelectedFaceup();
	}

	public int alignCards() {
		Card card=firstCard;
		int n=0;
		while(card!=null) {
			int y; if((type=='E')||(card.selected)) y=0; else y=20;
			card.setLocation(n*dx,n*dy+y);
			n++;
			card=card.nextCard;
		}
		return n;
	}

	public int remove(String[] mibol,char mit) {
		int k=-1;
		int l=mibol[0].length();
		for(int i=0;i<l;i++) 
			if(mibol[0].charAt(i)==mit) k=i;
		if(k!=-1) {
			String s=mibol[0].substring(0,k);
			s=s.concat(mibol[0].substring(k+1,l));
			mibol[0]=s;
		}
		return k;
	}
	public void removeAll() {
		Card card=firstCard;
		while(card!=null) {
			removeCard(card); 
			card=card.nextCard;
		}
		changedSelectedFaceup();
	}

	public void removeSelect() {
		Card card=firstCard;
		while(card!=null) {
			if(card.selected) removeCard(card); 
			card=card.nextCard;
		}
		changedSelectedFaceup();
	}

	public int removeRes(char pRes) {
		Card card=firstCard;
		int n=0;
		while(card!=null) {
			if(card.res==pRes) { removeCard(card); n++; } 
			card=card.nextCard;
		}
		changedSelectedFaceup();
		return n;
	}

	public char removeRandom() {
		Card card=firstCard;
		int r=S.R.nextInt(noCards()); int n=0;
		while(card!=null) {
			if(n++==r) {removeCard(card); return card.res;} 
			card=card.nextCard;
		}
		changedSelectedFaceup();
		return ' ';
	}

	public void removeFaceup(char pRes) {
		Card card=firstCard;
		while(card!=null) {
			if((!card.facedown)&&(card.res==pRes)) {removeCard(card);card=lastCard;} 
			card=card.nextCard;
		}
		changedSelectedFaceup();
	}
	public boolean isSelect(String pMit) {
		String[] mit = new String[1]; mit[0]=pMit;
		Card card=firstCard;
		boolean nemjo=false;
		while(card!=null) {
			if(card.selected) if(remove(mit,card.res)==-1) nemjo=true;
			card=card.nextCard;
		}
		return (mit[0].length()==0)&&!nemjo;
	}
	public boolean isMegvanBenne(String pMit) {
		String[] mit = new String[1]; mit[0]=pMit;
		Card card=firstCard;
		while(card!=null) {
			remove(mit,card.res);
			card=card.nextCard;
		}
		return (mit[0].length()==0);
	}
	public Card searchFacedownCard(char pRes) {
		Card result=null;
		Card card=firstCard;
		while(card!=null) {
			if((card.res==pRes)&&(card.facedown)) result=card;
			card=card.nextCard;
		}
		return result;
	}
	public boolean select(String pMit) {
		String[] mit = new String[1]; mit[0]=pMit;
		Card card=firstCard;
		while(card!=null) {
			if(card.selected) if(remove(mit,card.res)==-1) card.selected=false;
			card=card.nextCard;
		}
		for(int j=0;j<mit[0].length();j++) {
			card=firstCard;
			while(card!=null) {
				if(!card.selected) 
					if(remove(mit,card.res)!=-1) {card.selected=true;}
				card=card.nextCard;
			}
		}
		alignCards();
		changedSelectedFaceup();
		return mit[0].length()==0;
	}
	public void saveSelect() {
		Card card=firstCard;
		while(card!=null) {
			card.saveSelect=card.selected;
			card=card.nextCard;
		}		
	}
	public void restoreSelect() {
		Card card=firstCard;
		while(card!=null) {
			card.selected=card.saveSelect;
			card=card.nextCard;
		}
		alignCards();		
		changedSelectedFaceup();
	}
	public void changedSelectedFaceup() {
		parent.buttonEnabler();
	}
	public String getAllString() {
		nSelected=0;
		String sel = "";
		Card card=firstCard;
		while(card!=null) {
			nSelected++; 
			sel=sel.concat(new Character(card.res).toString());
			card=card.nextCard;
		}
		return sel;
	}
	public String getSelected() {
		nSelected=0;
		String sel = "";
		Card card=firstCard;
		while(card!=null) {
			if(card.selected) {
				nSelected++; 
				sel=sel.concat(new Character(card.res).toString());
			}
			card=card.nextCard;
		}
		return sel;
	}
	public String getFaceup(char pC) {
		nSelected=0;
		String sel = "";
		Card card=firstCard;
		while(card!=null) {
			if((!card.facedown)&&(card.res==pC)) {
				nSelected++; 
				sel=sel.concat(new Character(card.res).toString());
			}
			card=card.nextCard;
		}
		return sel;
	}
	public Card getDeselectRes(char pC) {
		Card result=null;
		Card card=firstCard;
		while(card!=null) {
			if((card.res==pC)&&(!card.selected)) {
				result=card;
			}
			card=card.nextCard;
		}
		return result;
	}
}