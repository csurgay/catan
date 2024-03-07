import javax.swing.*;

public class Longest {
	
	S parent;
	int longestRoad, longestPlayer;
	int largestArmy, largestPlayer;
	
	public Longest(S pParent) {
		parent=pParent;
	}
	
	public void init() {
		longestRoad=4; longestPlayer=0;
		largestArmy=2; largestPlayer=0;
		for(int i=1;i<=S.noPlayer;i++) parent.p[i].utLabel.setVisible(false);
		for(int i=1;i<=S.noPlayer;i++) parent.p[i].hadseregLabel.setVisible(false);
		parent.utLabel.setVisible(true);
		parent.hadseregLabel.setVisible(true);
	}

	// returns the player of the largest army if larger than largestArmy, 0 otherwise 
	public int largest() {
		int player=0;
		for(int i=1;i<=S.noPlayer;i++) {
			int l=parent.p[i].eDeck.getFaceup('l').length();
			if(l>largestArmy) {largestArmy=l;player=i;}
		}
		return player;
	}
	
	// returns the player of the longest road if longer than longestRoad, 0 otherwise 
	public int longestPlayer() {
		int player=0;
		for(int i=1;i<=S.noPlayer;i++) {
			int l=getLongestRoad(i);
			if(l>longestRoad) {longestRoad=l;player=i;}
		}
		return player;
	}
	
	// returns the longest road of pPlayer
	public int getLongestRoad(int pPlayer) {
		int result=0;
		Junction junc=parent.b.firstJunc;
		while(junc!=null) {
			int l=getLongest(junc,pPlayer);
			if(l>result) {result=l;}
			junc=junc.nextJunc;
		}
		return result;
	}
	
	protected int getLongest(Junction junc, int pl) {
		int l=0; int longer=0;
		for(int i=0;i<junc.nJuncs;i++)
			if(junc.road[i]==pl) {
				junc.removeUt(junc.juncs[i]);
				junc.juncs[i].removeUt(junc);
				l=1+getLongest(junc.juncs[i],pl);
				if(l>longer) longer=l;
				junc.addUt(junc.juncs[i],pl);
				junc.juncs[i].addUt(junc,pl);
			} 
		return longer;
	}
	
	public void setLongestRoad() {
		int pl=longestPlayer();
		if((pl>0)&&(pl!=longestPlayer)) { 
			parent.utLabel.setVisible(false);
			if(longestPlayer>0) {
				parent.p[longestPlayer].utLabel.setVisible(false);
				parent.p[longestPlayer].siegpunkt-=2;
			} 
			if(!parent.ps.allComp())
			JOptionPane.showMessageDialog(null,"is awarded to player "+new Integer(pl).toString(),
			"Longest road",JOptionPane.INFORMATION_MESSAGE,ImageLoader.kartya[1]);
			parent.p[pl].utLabel.setVisible(true);
			parent.p[pl].siegpunkt+=2;
			longestPlayer=pl;
		}
		pl=largest();
		if((pl>0)&&(pl!=largestPlayer)) { 
			parent.hadseregLabel.setVisible(false);
			if(largestPlayer>0) {
				parent.p[largestPlayer].hadseregLabel.setVisible(false);
				parent.p[largestPlayer].siegpunkt-=2;
			} 
			for(int i=1;i<=4;i++) parent.p[i].hadseregLabel.setVisible(false);
			if(!parent.ps.allComp())
			JOptionPane.showMessageDialog(null,"is awarded to player "+new Integer(pl).toString(),
			"Largest army",JOptionPane.INFORMATION_MESSAGE,ImageLoader.kartya[2]);
			parent.p[pl].hadseregLabel.setVisible(true);
			parent.p[pl].siegpunkt+=2;
			largestPlayer=pl;
		}
	}
	
}
