import javax.swing.*;

public class ImageLoader {

	// fontos a sorrend, ez alapjan: 1:erdo-fa, 2:agyag-tegla,...
	protected static String[] mezonevek = new String[] 
		{"sivatag","erdo","agyag","gabonatabla","legelo","hegy","tenger","kikoto"};
	protected static String[] resnevek = new String[] 
		{"hatlap","fa","tegla","buza","juh","ko"};
	public static ImageIcon[] mezo = new ImageIcon[mezonevek.length];
	public static ImageIcon[] res = new ImageIcon[resnevek.length];
	public static ImageIcon[] bigRes = new ImageIcon[resnevek.length];

	protected static String[] cardnevek = new String[] 
		{"hatlaps","lovag","utepito","felfedezes","monopol","egyetem",
		"templom","konyvtar","orszaghaz","piac"};
	public static ImageIcon[] cards = new ImageIcon[cardnevek.length];
	public static ImageIcon[] bigCards = new ImageIcon[cardnevek.length];

	protected static String[] kartyanevek = new String[] 
		{"arak","ut","hadsereg"};
	public static ImageIcon[] kartya = new ImageIcon[kartyanevek.length];

	protected static String[] buttonnevek = new String[] 
		{"becserel","csere","kovetkezo"};
	public static ImageIcon[] button = new ImageIcon[buttonnevek.length];
	
	public static ImageIcon[] playerIcon = new ImageIcon[7]; //1,2,3,4,5,6

	public ImageLoader(Settlers pSettlers) {
		for (int i=0;i<mezo.length;i++) mezo[i] = 
			pSettlers.getIcon(mezonevek[i]+".gif");
		for (int i=0;i<resnevek.length;i++) res[i] = 
			pSettlers.getIcon(resnevek[i]+".gif");
//		for (int i=0;i<resnevek.length;i++) bigRes[i] = 
//			pSettlers.getIcon(resnevek[i]+"232.jpg");
		for (int i=0;i<cardnevek.length;i++) cards[i] = 
			pSettlers.getIcon(cardnevek[i]+".gif");
		for (int i=0;i<cardnevek.length;i++) bigCards[i] = 
			pSettlers.getIcon(cardnevek[i]+"232.jpg");
		for (int i=0;i<kartyanevek.length;i++) kartya[i] = 
			pSettlers.getIcon(kartyanevek[i]+".gif");
		for (int i=0;i<buttonnevek.length;i++) button[i] = 
			pSettlers.getIcon(buttonnevek[i]+"Button.gif");
		for(int i=1;i<=6;i++) playerIcon[i] = // 6 players! 
			pSettlers.getIcon("pl"+"0123456".charAt(i)+"Icon.gif");
	}
	public static ImageIcon getMezoIcon(String ps) {
		int k=0;
		for(int i=0;i<mezo.length;i++) {
			if(ps.compareTo(mezonevek[i])==0) k=i;
		}
		return mezo[k];
	}
	public static int getPos(char pC, String[] pS) {
		int k=0;
		for(int i=0;i<pS.length;i++) {
			if(pS[i].charAt(0)==pC) k=i;
		}
		return k;
	}
	public static ImageIcon getResourceIcon(char pc) {
		return res[getPos(pc,resnevek)];
	}
	public static ImageIcon getBigResourceIcon(char pc) {
		return bigRes[getPos(pc,resnevek)];
	}
	public static ImageIcon getCardIcon(char pc) {
		return cards[getPos(pc,cardnevek)];
	}
	public static ImageIcon getBigCardIcon(char pc) {
		return bigCards[getPos(pc,cardnevek)];
	}
	public static char mezo2res(char pRes) {
		return resnevek[getPos(pRes,mezonevek)].charAt(0);
	}
}

