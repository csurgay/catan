
public class AI {
	
	static S parent;
	
	static int[] pontertekValue = new int[] {
		//   2  3  4  5  6  7  8  9 10 11 12
		0,0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1  // 36 dobasbol ennyi felette levo dobasertek lesz
	};
	
	static public void setParent(S pParent) {
		parent=pParent;
	}
	
	static Junction[] juncs = new Junction[100];
	static Junction[][] roads = new Junction[100][2];
	
	public static int getJuncValueSum(int pPlayer,Junction pJunc) {
		int sum=0;
		for(int j=0;j<pJunc.nTiles;j++) {
			int szorzo=1;
			if(parent.b.getPlayerSum(pPlayer,pJunc.tiles[j].resource)<3) szorzo+=6;
			if(parent.b.vanKikotoje(pPlayer,pJunc.tiles[j].resource)) szorzo+=4;
			sum+=szorzo*pontertekValue[pJunc.tiles[j].pontertek];
		}
		if(pJunc.resource=='3') { if(!parent.b.vanKikotoje(pPlayer,'3')) sum+=3; }
		else if(pJunc.resource!=' ') {
			if(parent.b.getPlayerSum(pPlayer,pJunc.resource)>3) sum+=2;
			else if(parent.b.getPlayerSum(pPlayer,pJunc.resource)>6) sum+=3;
			else if (parent.b.getPlayerSum(pPlayer,pJunc.resource)>10) sum+=6;
			else if (parent.b.getPlayerSum(pPlayer,pJunc.resource)>14) sum+=12;
		}
		return sum;
	}

	// all juncs where pPlayer can place a house, no road needed if pFirstTwoRounds
	public static Junction[] getAvailableHazak(int pPlayer, boolean pFirstTwoRounds) {
		int nJuncs=0;
		Junction[] result;
		Junction junc=parent.b.firstJunc;
		while(junc!=null) {
			boolean available=true;
			if(junc.haz.type>0) available=false;
			if(junc.vanszomszed()) available=false;
			if((!junc.vanideut(pPlayer))&&(!pFirstTwoRounds)) available=false;
			if(available) juncs[nJuncs++]=junc;
			junc=junc.nextJunc;
		}
		result=new Junction[nJuncs];
		for(int i=0;i<nJuncs;i++) result[i]=juncs[i];
		return result;
	}
	
	// gives the highest out of getAvailableHazak
	public static Junction getBestHaz(int pPlayer,boolean pFirstTwoRounds) {
		Junction[] juncs = getAvailableHazak(pPlayer,pFirstTwoRounds);
		int max=0;
		Junction bestJunc=null;
		for(int i=0;i<juncs.length;i++) {
			int sum=getJuncValueSum(pPlayer,juncs[i]);
			if(sum>max) { max=sum; bestJunc=juncs[i]; }
		}
		return bestJunc;
	}
	
	// all juncs where pPlayer can place a road
	public static Junction[][] getOneRoads(int pPlayer) {
		int nRoads=0;
		Junction[][] result;
		Junction junc=parent.b.firstJunc;
		Junction junc2=null;
		while(junc!=null) {
			boolean egyUttalElerheto=false;
			for(int i=0;i<junc.nJuncs;i++) 
			if(junc.juncs[i].vanideut(pPlayer)  // van oda utja, vagy van ott haza 
				||((junc.juncs[i].haz.type>0)
					&&(junc.juncs[i].haz.player==pPlayer))) 
			if((junc.road[i]==0)				// nincs meg ott masik ut, es nincs masnak haza ott
			&&((junc.juncs[i].haz.type==0)||(junc.juncs[i].haz.player==pPlayer))) {
				junc2=junc.juncs[i]; egyUttalElerheto=true;
			}
			if(egyUttalElerheto) roads[nRoads++]=new Junction[]{junc,junc,junc2};
			junc=junc.nextJunc;
		}
//		if(parent.p[pPlayer].noUt+1>Player.maxNoUt) nRoads=0;
		result=new Junction[nRoads][3];
		for(int i=0;i<nRoads;i++) result[i]=roads[i];
		return result;
	}
	
	// all juncs where pPlayer can place a house with a help of one road
	public static Junction[][] getAvailableOneRoads(int pPlayer) {
		int nRoads=0;
		Junction[][] result;
		Junction junc=parent.b.firstJunc;
		Junction junc2=null;
		while(junc!=null) {
			boolean available=true;
			if(junc.haz.type>0) available=false;
			if(junc.vanszomszed()) available=false;
			if(junc.vanideut(pPlayer)) available=false;
			boolean egyUttalElerheto=false;
			for(int i=0;i<junc.nJuncs;i++) 
			if(junc.juncs[i].vanideut(pPlayer)
				||((junc.juncs[i].haz.type>0)
					&&(junc.juncs[i].haz.player==pPlayer))) 
			if((junc.road[i]==0)&&((junc.juncs[i].haz.type==0)||(junc.juncs[i].haz.player==pPlayer))) {
				junc2=junc.juncs[i]; egyUttalElerheto=true;
			}
			if(!egyUttalElerheto) available=false;
			if(available) roads[nRoads++]=new Junction[]{junc,junc,junc2};
			junc=junc.nextJunc;
		}
		if(parent.p[pPlayer].noUt+1>Player.maxNoUt) nRoads=0;
		result=new Junction[nRoads][3];
		for(int i=0;i<nRoads;i++) result[i]=roads[i];
		return result;
	}
	
	// all juncs where pPlayer can place two roads
	public static Junction[][] getTwoRoads(int pPlayer) {
		int nRoads=0;
		Junction[][] result;
		Junction junc=parent.b.firstJunc;
		Junction junc2=null, junc3=null;
		while(junc!=null) {
			boolean ketUttalElerheto=false;
			for(int i=0;i<junc.nJuncs;i++) for(int j=0;j<junc.juncs[i].nJuncs;j++)
			if(junc!=junc.juncs[i].juncs[j])	// nem oda-vissza ut
			if((junc.juncs[i].juncs[j].vanideut(pPlayer))
				||((junc.juncs[i].juncs[j].haz.type>0)
					&&(junc.juncs[i].juncs[j].haz.player==pPlayer))) 
			if((junc.road[i]==0)&&(junc.juncs[i].road[j]==0)
			&&((junc.juncs[i].haz.type==0)||(junc.juncs[i].haz.player==pPlayer))
			&&((junc.juncs[i].juncs[j].haz.type==0)||(junc.juncs[i].juncs[j].haz.player==pPlayer))) {
				junc2=junc.juncs[i]; 
				junc3=junc.juncs[i].juncs[j]; 
				ketUttalElerheto=true;
			}
			if(ketUttalElerheto) roads[nRoads++]=new Junction[]{junc,junc2,junc3};
			junc=junc.nextJunc;
		}
//		if(parent.p[pPlayer].noUt+2>Player.maxNoUt) nRoads=0;
		result=new Junction[nRoads][3];
		for(int i=0;i<nRoads;i++) result[i]=roads[i];
		return result;
	}
	
	// all juncs where pPlayer can place a house with a help of two roads
	public static Junction[][] getAvailableTwoRoads(int pPlayer,Junction pFirstTwoHaz) {
		int nRoads=0;
		Junction[][] result;
		Junction junc=parent.b.firstJunc;
		Junction junc2=null, junc3=null;
		while(junc!=null) {
			boolean available=true;
			if(junc.haz.type>0) available=false;
			if(junc.vanszomszed()) available=false;
			if(junc.vanideut(pPlayer)) available=false;
			boolean ketUttalElerheto=false;
			for(int i=0;i<junc.nJuncs;i++) for(int j=0;j<junc.juncs[i].nJuncs;j++)
			if((junc.juncs[i].juncs[j].vanideut(pPlayer)) 
				||((junc.juncs[i].juncs[j].haz.type>0)
					&&(junc.juncs[i].juncs[j].haz.player==pPlayer))) 
			if((junc.road[i]==0)&&(junc.juncs[i].road[j]==0)&&(!junc.juncs[i].vanideut(pPlayer))
			&&((junc.juncs[i].haz.type==0)||(junc.juncs[i].haz.player==pPlayer))
			&&((junc.juncs[i].juncs[j].haz.type==0)||(junc.juncs[i].juncs[j].haz.player==pPlayer))) {
				junc2=junc.juncs[i]; 
				junc3=junc.juncs[i].juncs[j]; 
				ketUttalElerheto=true;
			}
			if(!ketUttalElerheto) available=false;
			if(pFirstTwoHaz!=null) if(junc3!=pFirstTwoHaz) available=false;
			if(available) roads[nRoads++]=new Junction[]{junc,junc2,junc3};
			junc=junc.nextJunc;
		}
		if(parent.p[pPlayer].noUt+2>Player.maxNoUt) nRoads=0;
		result=new Junction[nRoads][3];
		for(int i=0;i<nRoads;i++) result[i]=roads[i];
		return result;
	}
	
	// all juncs where pPlayer can place a house with a help of three roads
	public static Junction[][] getAvailableThreeRoads(int pPlayer,Junction pFirstTwoHaz) {
		int nRoads=0;
		Junction[][] result;
		Junction junc=parent.b.firstJunc;
		Junction junc2=null, junc3=null;
		while(junc!=null) {
			boolean available=true;
			if(junc.haz.type>0) available=false;
			if(junc.vanszomszed()) available=false;
			if(junc.vanideut(pPlayer)) available=false;
			boolean haromUttalElerheto=false;
			for(int i=0;i<junc.nJuncs;i++) for(int j=0;j<junc.juncs[i].nJuncs;j++)
			for(int k=0;k<junc.juncs[i].juncs[j].nJuncs;k++)
			if((junc.juncs[i].juncs[j].juncs[k].vanideut(pPlayer)) 
				||((junc.juncs[i].juncs[j].juncs[k].haz.type>0)
					&&(junc.juncs[i].juncs[j].juncs[k].haz.player==pPlayer))) 
			{
				if((junc!=junc.juncs[i].juncs[j])
				&&(junc.juncs[i]!=junc.juncs[i].juncs[j].juncs[k])) 
				if((junc.road[i]==0)&&(junc.juncs[i].road[j]==0)
				&&(junc.juncs[i].juncs[j].road[k]==0)&&(!junc.juncs[i].vanideut(pPlayer))
				&&((junc.juncs[i].haz.type==0)||(junc.juncs[i].haz.player==pPlayer))
				&&((junc.juncs[i].juncs[j].haz.type==0)||(junc.juncs[i].juncs[j].haz.player==pPlayer))
				&&((junc.juncs[i].juncs[j].juncs[k].haz.type==0)||(junc.juncs[i].juncs[j].juncs[k].haz.player==pPlayer))){
					junc2=junc.juncs[i].juncs[j]; 
					junc3=junc.juncs[i].juncs[j].juncs[k]; 
					haromUttalElerheto=true;
				}
			}
			if(!haromUttalElerheto) available=false;
			if(pFirstTwoHaz!=null) if(junc3!=pFirstTwoHaz) available=false;
			if(available) roads[nRoads++]=new Junction[]{junc,junc2,junc3};
			junc=junc.nextJunc;
		}
		if(parent.p[pPlayer].noUt+3>Player.maxNoUt) nRoads=0;
		result=new Junction[nRoads][3];
		for(int i=0;i<nRoads;i++) result[i]=roads[i];
		return result;
	}
	
	public static Junction[] getBestRoad(int pPlayer,Junction pFirstTwoHaz) {
		int sum; Junction[][] roads=null;
		int max1=0; Junction[] result1=null; 
		if(pFirstTwoHaz==null) {
			roads=getAvailableOneRoads(pPlayer); 
			for(int i=0;i<roads.length;i++) {
				sum=getJuncValueSum(pPlayer,roads[i][0]);
				if(sum>max1) { max1=sum; result1=roads[i]; }
			}
		}
		int max2=0; Junction[] result2=null; 
		roads=getAvailableTwoRoads(pPlayer,pFirstTwoHaz);
		for(int i=0;i<roads.length;i++) {
			sum=getJuncValueSum(pPlayer,roads[i][0]);
			if(sum>max2) { max2=sum; result2=roads[i]; }
		}
		int max3=0; Junction[] result3=null; 
		roads=getAvailableThreeRoads(pPlayer,pFirstTwoHaz);
		for(int i=0;i<roads.length;i++) {
			sum=getJuncValueSum(pPlayer,roads[i][0]);
			if(sum>max3) { max3=sum; result3=roads[i]; }
		}
		Junction[] result=result1; int max=max1;
		if((result==null)||(max2>max+2)) { result=result2; max=max2; } 
		if((result==null)||(max3>max+2)) { result=result3; max=max3; } 
		return result;
	}
/*	
	public static Junction[] getLongestRoad(int pPlayer) {
		Junction[] result=null;
		int max=0;
		Junction junc=parent.b.firstJunc;
		Junction junc2;
		while(junc!=null) {
			int sum=parent.longest.getLongest(junc,pPlayer);
			junc2=null;
			for(int i=0;i<junc.nJuncs;i++) if(junc.road[i]==0) junc2=junc.juncs[i];
			if((sum>max)&&(junc2!=null)) { max=sum; result=new Junction[]{null,junc,junc2}; }
			junc=junc.nextJunc;
		}
		return result;
	}
*/	
	public static Junction[] getLongestRoad(int pPlayer) {
		Junction[] result=null; Junction[][] roads; int longest;
		int max=0; 
		roads = getOneRoads(pPlayer);
		for(int i=0;i<roads.length;i++) {
			roads[i][1].addUt(roads[i][2],pPlayer);
			roads[i][2].addUt(roads[i][1],pPlayer);
			longest=parent.longest.getLongestRoad(pPlayer);
			roads[i][1].removeUt(roads[i][2]);
			roads[i][2].removeUt(roads[i][1]);
			if(longest>max) {max=longest;result=roads[i];}
		}
		roads = getTwoRoads(pPlayer);
		for(int i=0;i<roads.length;i++) {
			roads[i][0].addUt(roads[i][1],pPlayer);
			roads[i][1].addUt(roads[i][0],pPlayer);
			roads[i][1].addUt(roads[i][2],pPlayer);
			roads[i][2].addUt(roads[i][1],pPlayer);
			longest=parent.longest.getLongestRoad(pPlayer);
			roads[i][0].removeUt(roads[i][1]);
			roads[i][1].removeUt(roads[i][0]);
			roads[i][1].removeUt(roads[i][2]);
			roads[i][2].removeUt(roads[i][1]);
			if(longest>max) {max=longest;result=roads[i];}
		}
		return result;
	}
	
	public static Junction getBestVaros(int pPlayer) {
		Junction result=null;
		Junction junc=parent.b.firstJunc;
		int max=0;
		while(junc!=null) {
			int sum=getJuncValueSum(pPlayer,junc);
			if((junc.haz.type==1)&&(junc.haz.player==pPlayer)&&(sum>max)) { 
				max=sum; result=junc; 
			}
			junc=junc.nextJunc;
		}
		return result;
	}

	public static Tile getTileForRablo(int pPlayer, Tile rabloTile) {
		int max=0;
		Tile bestTile=parent.b.firstTile;
		Tile tile=parent.b.firstTile;
		while(tile!=null) {
			int sum=0;
			boolean sajatis=false;
			for(int i=0;i<6;i++) {
				sum+=pontertekValue[tile.pontertek]*tile.juncs[i].haz.type;
				if((tile.juncs[i].haz.type>0)&&(tile.juncs[i].haz.player==pPlayer))
					sajatis=true;
			} 
			if((sum>max)&&(!sajatis)&&(tile!=rabloTile)) {
				max=sum;
				bestTile=tile;
			}
			tile=tile.nextTile;
		}
		return bestTile;
	}
	
	public static boolean rabloHurts(int pPlayer) {
		boolean hurts=false;
		for(int i=0;i<6;i++) if(parent.b.rabloTile.type!='S') {
			if((parent.b.rabloTile.juncs[i].haz.type>0)
				&&(parent.b.rabloTile.juncs[i].haz.player==pPlayer)) hurts=true;
		}
		return hurts;
	}
	
	public static Card searchFacedownCard(char pRes,int pPlayer) {
		return parent.p[pPlayer].eDeck.searchFacedownCard(pRes);
	}
		
	public static Card searchSiegpunktCard(int pPlayer) {
		Card result=null;
		if(result==null) result=parent.p[pPlayer].eDeck.searchFacedownCard('p');
		if(result==null) result=parent.p[pPlayer].eDeck.searchFacedownCard('o');
		if(result==null) result=parent.p[pPlayer].eDeck.searchFacedownCard('e');
		if(result==null) result=parent.p[pPlayer].eDeck.searchFacedownCard('k');
		if(result==null) result=parent.p[pPlayer].eDeck.searchFacedownCard('t');
		return result;
	}
}
