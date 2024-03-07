import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class Board extends JComponent {
	int meret;

	S parent;
	int locx,locy;
	protected String[] nodetype;
	
	protected int cx; 
	protected int cy;
	Node[][] grid;
	
	Node firstNode, lastNode;
	Tile firstAllTile, allTile, lastAllTile; // all tiles, including tenger es sivatag
	Tile firstTile, tile, lastTile; // normal tiles
	Junction firstJunc, junc, lastJunc; // junctions
	int nJunc;
	Tile rabloTile;
	final int[][] sz = new int[][] {{-1,-1},{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0}};

	public void create() {
		if(meret==34) {
			nodetype = new String[] {
					"UUUUUTUUUUU",
					"UUUKJJKUUUU",
					"UUTJJNJJTUU",
					"KJJNJJNJJKU",
					"UJNJJNJJNJU",
					"TJJNJJNJJTU",
					"UJNJJSJJNJU",
					"KJJNJJNJJKU",
					"UJNJJNJJNJU",
					"TJJNJJNJJTU",
					"UUKJJNJJKUU",
					"UUUTJJTUUUU",
					"UUUUUKUUUUU"
			};		
		} else if(meret==56) {
			nodetype = new String[] {
				"UUUUUUUKUUUUUU",
				"UUUUUTJJTUUUUU",
				"UUUUKJJNJJKUUU",
				"UUTJJNJJNJJTUU",
				"UKJJNJJNJJNJJK",
				"UJNJJSJJNJJNJU",
				"UTJJNJJNJJNJJT",
				"UJNJJNJJNJJNJU",
				"UKJJNJJNJJNJJK",
				"UJNJJNJJSJJNJU",
				"UTJJNJJNJJNJJT",
				"UUKJJNJJNJJKUU",
				"UUUUTJJNJJTUUU",
				"UUUUUKJJKUUUUU",
				"UUUUUUUTUUUUUU"
			   };
		} else if(meret==56) {
			nodetype = new String[] {
				"UTUUTUUKUUTUUT",
				"UUTUUTJJTUUTUU",
				"UTUUKJJNJJKUUT",
				"UUTJJNJJNJJTUU",
				"UKJJNJJNJJNJJK",
				"UJNJJSJJNJJNJU",
				"UTJJNJJNJJNJJT",
				"UJNJJNJJNJJNJU",
				"UKJJNJJNJJNJJK",
				"UJNJJNJJSJJNJU",
				"UTJJNJJNJJNJJT",
				"UUKJJNJJNJJKUU",
				"UTUUTJJNJJTUUT",
				"UUTUUKJJKUUTUU",
				"UTUUTUUTUUTUUT",
				"UUTUUTUUTUUTUU"
			   };
		}
		cx=nodetype[0].length(); cy=nodetype.length; 
		grid = new Node[cx][cy];
		
		firstNode=null; lastNode=null; firstTile=null; lastTile=null; firstJunc=null; 
		lastJunc=null; rabloTile=null; firstAllTile=null; lastAllTile=null; nJunc=0;

		for(int i=0;i<cx;i++) for(int j=0;j<cy;j++) {
			int x = locx + i*50+(j%2)*25; int y = locy + j*43;
//			int x = locx + i*45+(j%2)*25; int y = locy + j*39;
			char type = nodetype[j].charAt(i);
			if(type=='J') { // Junction type Node
				junc = new Junction(x,y,nJunc++);
				junc.haz=new Haz(0,0,false,parent); 
				junc.haz.junc=junc;
				junc.haz.setLocation(x-9,y-13);
				grid[i][j] = junc;
				if(lastNode==null) firstNode=junc; else lastNode.nextNode=junc;
				lastNode = junc; 
				if(lastJunc==null) firstJunc=junc; else lastJunc.nextJunc=junc; 
				lastJunc = junc;
			}
			else if(type!='U') { // Tile type Node (K-ikoto,T-enger,N-ormal)
				tile = new Tile(x,y);
				tile.type = type;
				grid[i][j] = tile;
				if(lastNode==null) firstNode=tile; else lastNode.nextNode=tile;
				lastNode = tile;
				if(lastAllTile==null) firstAllTile=tile; else lastAllTile.nextAllTile=tile;
				lastAllTile = tile;
				if(type=='N') {
					if(lastTile==null) firstTile=tile; else lastTile.nextTile=tile;
					lastTile = tile;
				}
			}
		}
		for(int i=0;i<cx;i++) for(int j=0;j<cy;j++) {
			char type = nodetype[j].charAt(i);
			if(type=='N') {
				tile = (Tile)grid[i][j];
				int nJ=0;
				// szomszed Tile-ok eltarolasa
				for(int k=-2;k<3;k++) for(int l=-2;l<3;l++)
					if((j+l>=0)&&(i+k>=0)) // felul es baloldalt ne menjen negativba a kereses
						if((nodetype[j+l].charAt(i+k)=='N')&&(k*k+l*l!=0))  // sajat magat nem
							tile.tiles[tile.nTiles++]=(Tile)grid[i+k][j+l];
				junc=null;
				// korulvevo Junction-ok eltarolasa
				for(int k=0;k<8;k++) {
					int mi=i+sz[k][0]; int mj=j+sz[k][1];
					if(nodetype[mj].charAt(mi)=='J') {
						Junction mJunc = (Junction)grid[mi][mj];
						tile.juncs[nJ++]=mJunc;
						mJunc.addTile(tile);
						if(junc==null) {
							junc=mJunc; lastJunc=mJunc;
						}
						else {
							junc.addJunc(mJunc);
							(mJunc).addJunc(junc);
							junc=mJunc;
						}
					}
				}
				lastJunc.addJunc(junc);
				junc.addJunc(lastJunc);
			}
		}
		// Kikotok melletti Junction-ok megjegyzese, hogy majd oda menjen a vonal
		for(int i=0;i<cx;i++) for(int j=0;j<cy;j++) {
			char type = nodetype[j].charAt(i);
			if(type=='J') {
				Junction junc = (Junction)grid[i][j];
				for(int k=0;k<8;k++) {
					int mi=i+sz[k][0]; int mj=j+sz[k][1];
					if(nodetype[mj].charAt(mi)=='K') {
						Tile tile = (Tile)grid[mi][mj];
						if(tile.nJuncs<2) tile.juncs[tile.nJuncs++] = junc;
						else if(junc.nTiles>1) tile.juncs[1] = junc;
					}
				}
			}
		}
		for(int i=0;i<cx;i++) for(int j=0;j<cy;j++) {
			char type = nodetype[j].charAt(i);
			if(type=='K') {
				Tile tile = (Tile)grid[i][j];
				for(int k=0;k<2;k++) {
					Junction junc = tile.juncs[k];
					if(tile.y==junc.y) 
						{if(tile.x>junc.x) tile.kO[9]=true; else tile.kO[3]=true;}
					else if(tile.y>junc.y) 
						{if(tile.x>junc.x) tile.kO[11]=true; else tile.kO[1]=true;}
					else if(tile.y<junc.y) 
						{if(tile.x>junc.x) tile.kO[7]=true; else tile.kO[5]=true;}
				}
			}
		}
	}

	public void addHazak() {
		// haz componensek hozzaadasa s-hez
		Junction junc=firstJunc; while(junc!=null) {
			parent.add(junc.haz);
			junc=junc.nextJunc;
		}
	}

	public void removeHazak() {
		// haz componensek torlese s-bol
		Junction junc=firstJunc; while(junc!=null) {
			parent.remove(junc.haz);
			junc=junc.nextJunc;
		}
	}

	Random R = new Random();
	protected int[] constKikotok;
	protected int[] kikotok;
	protected int[] constMezok;
	protected int[] mezok;
	protected int[] ertekek;
	int[] e68;
	protected char[] kikotoBetuk = new char[] {'3','f','t','b','j','k'};
	protected String[] mezoNormalNevek = new String[] {"erdo","agyag","gabonatabla","legelo","hegy"};
	
	// tabla "felrakasa", lehet hivni uj jatek elott
	public void init() {
		if(meret==34) {
			constKikotok = new int[] { 4,1,1,1,1,1 };
			constMezok = new int[] { 4,3,4,4,3,9,1,9 };
			ertekek = new int[] {2,3,4,5,9,10,11,12,3,4,5,9,10,11};
			e68 = new int[] {6,6,8,8};
		} else if(meret==56) {
			constKikotok = new int[] { 5,1,1,1,2,1 };
			constMezok = new int[] { 6,5,6,6,5,11,2,11 };
			ertekek = new int[] {2,3,4,5,9,10,11,12,3,4,5,9,10,11,2,3,4,5,9,10,11,12};
			e68 = new int[] {6,6,6,8,8,8};
		}
		kikotok = new int[constKikotok.length];
		mezok = new int[constMezok.length];

		for(int k=0;k<constKikotok.length;k++) kikotok[k]=constKikotok[k];
		for(int k=0;k<constMezok.length;k++) mezok[k]=constMezok[k];
		tile=firstAllTile; while(tile!=null) {
			if(tile.type=='T') tile.icon=ImageLoader.getMezoIcon("tenger");
			else if(tile.type=='K') {
				tile.icon=ImageLoader.getMezoIcon("kikoto");
				int sumkikocc = 0;
				for(int k=0;k<kikotok.length;k++) sumkikocc += kikotok[k];
				int r = R.nextInt(sumkikocc)+1;
				int k=0; int kr=0; 
				while (kr<r) { kr+=kikotok[k]; k++; };
				k--; kikotok[k]--;
				tile.resource = kikotoBetuk[k];
				tile.resIcon = ImageLoader.getResourceIcon(kikotoBetuk[k]);
				for(int i=0;i<tile.nJuncs;i++) tile.juncs[i].resource=tile.resource;
			} else if (tile.type=='N') {
				int sumnormocc = 0;
				for(int k=0;k<5;k++) sumnormocc += mezok[k];
				int r = R.nextInt(sumnormocc)+1;
				int k=0; int kr=0; 
				while (kr<r) { kr+=mezok[k]; k++;	};
				k--; mezok[k]--;
				tile.resource = mezoNormalNevek[k].charAt(0);
				tile.icon = ImageLoader.getMezoIcon(mezoNormalNevek[k]);
				tile.pontertek=0;
			} else if (tile.type=='S') {
				tile.icon = ImageLoader.getMezoIcon("sivatag");
				rabloTile=tile;
				tile.hasRablo=true;
				parent.rablo.setLocation(tile.x+10,tile.y-25);
			}
			tile=tile.nextAllTile;
		}
		
		// pontertekek kiosztasa, 6-8 nem lehet egymas mellett
		int nNode = ertekek.length+e68.length; 
		int maradekNode;
		// place 6s and 8s
		for(int j=0;j<e68.length;j++) {
			tile=getPosTile(R.nextInt(nNode--)+1,0); tile.pontertek=e68[j];
			for(int i=0;i<tile.nTiles;i++) 
				if(tile.tiles[i].pontertek==0) {nNode--; tile.tiles[i].pontertek = 1;}
		}
		// place the rest point values
		maradekNode=ertekek.length; for(int i=0;i<ertekek.length;i++) {
			tile=getPosTile(R.nextInt(maradekNode--)+1,1);
			tile.pontertek=ertekek[i];
		}
		
		// utak, hazak torlese
		Junction junc=firstJunc; while(junc!=null) {
			junc.haz.type=0;
			junc.haz.player=0;
			for(int i=0;i<3;i++) junc.road[i]=0;
			junc=junc.nextJunc;
		}
	}

	public boolean vanKikotoje(int pPl,char pRes) {
		boolean igen=false;
		Junction junc=firstJunc;
		while(junc!=null) {
			if((junc.haz.player==pPl)&&(junc.haz.type>0)
				&&(junc.resource==pRes)) igen=true;
			junc=junc.nextJunc;
		}
//		U.o(new Character(pRes).toString()+' '+(new Boolean(igen).toString()));
		return igen;
	}

	public int getPlayerSum(int pPlayer, char pRes) {
		int sum=0;
		Tile tile=firstTile;
		while(tile!=null) {
			for(int i=0;i<6;i++) if(tile.resource==pRes)
				if((tile.juncs[i].haz.type>0)&&(tile.juncs[i].haz.player==pPlayer))
					sum+=tile.juncs[i].haz.type*AI.pontertekValue[tile.pontertek];
			tile=tile.nextTile;
		}
		return sum;
	}

	protected Tile getPosTile(int n,int compare) {
		Tile ptile = firstTile;
		while(ptile!=null) {
			if(ptile.pontertek<=compare) n--;
			if(n==0) return ptile;
			ptile=ptile.nextTile;
		}
		return null;
	}

	public Node getNearestNode(int px,int py,int d) {
		Node nearest = null;
		int min = d*d; int dx; int dy; int m=0;
		Node node = firstNode; while(node!=null) {
			dx = px - node.x; dy = py - node.y; m = dx*dx+dy*dy;
			if(m<min) {nearest=node; min=m;}
			node = node.nextNode;
		}
		if(nearest!=null) nearest.distance = m;
		return nearest;
	}

	public Tile getNearestTile(int px,int py,int d) {
		Tile nearest = null;
		int min = d*d; int dx; int dy; int m=0;
		Tile tile = firstAllTile; while(tile!=null) {
			dx = px - tile.x; dy = py - tile.y; m = dx*dx+dy*dy;
			if(m<min) {nearest=tile; min=m;}
			tile = tile.nextAllTile;
		}
		if(nearest!=null) nearest.distance = m;
		return nearest;
	}
	
	public Junction getNearestJunc(int px,int py,int d) {
		Junction nearest = null;
		int min = d*d; int dx; int dy; int m=0;
		Junction junc = firstJunc; while(junc!=null) {
			dx = px - junc.x; dy = py - junc.y; m = dx*dx+dy*dy;
			if(m<min) {nearest=junc; min=m;}
			junc = junc.nextJunc;
		}
		if(nearest!=null) nearest.distance = m;
		return nearest;
	}
	
	protected void paintComponent(Graphics gg) {

		Graphics2D g = (Graphics2D)gg.create();
		
		g.setFont( new Font( "Times New Roman", Font.BOLD, 16 ));
		
		Tile tile = firstAllTile; while(tile!=null) {
			char mt=tile.type; char r=tile.resource;
			int x=tile.x-49; int y=tile.y-43;
			if((mt=='K')||(mt=='T')||(mt=='N')||(mt=='S')) {
				Polygon pol = getMask(x,y);
				g.setClip(pol);
				g.drawImage(tile.icon.getImage(),x,y,this);
				if(mt=='K') {
					g.setColor(Color.black);
					if(tile.kO[3]) {g.drawArc(x+89,y+33,20,20,0,360);g.drawLine(x+49,y+43,x+99-10,y+43);}
					if(tile.kO[5]) {g.drawArc(x+64,y+76,20,20,0,360);g.drawLine(x+49,y+43,x+74-5,y+86-9);}
					if(tile.kO[7]) {g.drawArc(x+15,y+76,20,20,0,360);g.drawLine(x+49,y+43,x+25+5,y+86-9);}
					if(tile.kO[9]) {g.drawArc(x-10,y+33,20,20,0,360);g.drawLine(x+49,y+43,x+10,y+43);}
					if(tile.kO[11]) {g.drawArc(x+15,y-10,20,20,0,360);g.drawLine(x+49,y+43,x+25+6,y+8);} 
					if(tile.kO[1]) {g.drawArc(x+64,y-10,20,20,0,360);g.drawLine(x+49,y+43,x+74-6,y+8);}
					Ellipse2D.Double circle = new Ellipse2D.Double(x+33,y+27,33,33);
					if(r!='3') {
						g.setClip(circle);
						g.drawImage(tile.resIcon.getImage(),x+21,y-3,this);
					}
					else {
						g.setColor(U.kikotoColor);
						g.fillOval(x+34,y+28,31,31);
						g.setColor(Color.black);
						g.drawChars(new char[]{'3',':','1'},0,3,x+40,y+50);
					};
				} else if((mt=='N')) {
					int e = tile.pontertek;
					g.setColor(U.pontertekColor);
					g.fillOval(x+35,y+29,29,29);
					g.setColor(Color.black);
					if ((e==6)||(e==8)) g.setColor(Color.red);
					char[] c = new char[2];
					if (e>9) {
						new Integer(e).toString().getChars(0,2,c,0);
						g.drawChars(c,0,2,x+42,y+50);
					} else {
						new Integer(e).toString().getChars(0,1,c,0);
						g.drawChars(c,0,1,x+45,y+50);
					}
//					for(int k=0;k<tile.nTiles;k++)
//						g.drawLine(tile.x,tile.y,tile.tiles[k].x,tile.tiles[k].y);
//					for(int k=0;k<6;k++)
//						g.drawLine(tile.x,tile.y,tile.juncs[k].x,tile.juncs[k].y);
				}
			}
			tile=tile.nextAllTile;
		}
		g.setClip(0,0,1000,1000);
		int[][] hvPx = new int[][] {{0,7,14,14, 0, 0, 0},{0,6,12,12,24,24, 0}};
		int[][] hvPy = new int[][] {{7,0, 7,16,16,16,16},{6,0, 6,10,10,21,21}};

		// utak rajzolasa
		g.setStroke(new BasicStroke(6.0f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		Junction junc = firstJunc; while(junc!=null) {
			int x=junc.x-49; int y=junc.y-43;
			for(int k=0;k<3;k++) {
				int pl = junc.road[k];
				if(pl>0) {
					g.setColor(U.playerColor[pl]);
					int mx=junc.juncs[k].x;
					int my=junc.juncs[k].y;
					g.drawLine(x+49+(mx-x-49)/6,y+43+(my-y-43)/6,mx-(mx-x-49)/6,my-(my-y-43)/6);
				}
			}
//			for(int k=0;k<junc.nJuncs;k++)
//				g.drawLine(junc.x,junc.y,junc.juncs[k].x,junc.juncs[k].y);
//			for(int k=0;k<junc.nTiles;k++)
//				g.drawLine(junc.x,junc.y,junc.tiles[k].x,junc.tiles[k].y);
			junc=junc.nextJunc;
		}
		
		// hazak rajzolasa
		g.setStroke(new BasicStroke(1.5f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		junc = firstJunc; while(junc!=null) {
			junc.haz.setVisible(true);
			junc=junc.nextJunc;
		}

		if(S.internals) {
			char[] c = new char[1];
			tile=firstAllTile; while(tile!=null) {
				c[0]=tile.resource; 
				g.drawChars(c,0,1,tile.x,tile.y);
				if (tile.type=='N') g.drawArc(tile.x-15,tile.y-15,30,30,0,360);
				else if (tile.type=='K') g.drawArc(tile.x-25,tile.y-25,50,50,0,360);
				else g.drawArc(tile.x-5,tile.y-5,10,10,0,360);
				tile=tile.nextAllTile;
			}
			junc=firstJunc; while(junc!=null) {
				c[0]=junc.resource; 
				g.drawChars(c,0,1,junc.x,junc.y);
				g.drawArc(junc.x-15,junc.y-15,30,30,0,360);
				junc=junc.nextJunc;
			}
		}
				g.dispose();
	}

	protected Polygon getMask(int x, int y) {
		Polygon pol = new Polygon();
		pol.addPoint(x+25,y);
		pol.addPoint(x+74,y);
		pol.addPoint(x+99,y+43);
		pol.addPoint(x+74,y+86);
		pol.addPoint(x+25,y+86);
		pol.addPoint(x,y+43);
		return pol;
	}
	
	public void collect(int pV) {
		if(pV==7) {
			parent.rablo.active=true;
			U.o(" -- Hetes dobas"); // Report for Java console
			parent.repaint();
			parent.rablo.throwHalf();
		} else {
			Tile tile = firstTile; while(tile!=null) {
				if((tile.hasRablo==false)&&(tile.pontertek==pV)) for(int i=0;i<6;i++) {
					Junction junc = tile.juncs[i];
					for(int j=0;j<junc.haz.type;j++) {
						char res = ImageLoader.mezo2res(tile.resource);
						parent.p[junc.haz.player].deck.addCard('R',res);
					}
				}
				tile=tile.nextTile;
			}
		}
	}
	
	public Board(S pParent,int pLocx,int pLocy) {
		parent=pParent;
		locx=pLocx;
		locy=pLocy;
//		setBounds(locx,locy,500,600); setBorder(new javax.swing.border.BevelBorder(1));
		setBounds(0,0,1000,1000); // setBorder(new javax.swing.border.BevelBorder(1));
	}
}
