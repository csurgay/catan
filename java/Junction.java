public class Junction extends Node {

	public Haz haz;
	
	// sorszama
	public int no;

	// szomszedos mezok
	protected int nTiles;
	public Tile[] tiles = new Tile[3];

	// szomszedok, max 3 db
	protected int nJuncs;
	public Junction[] juncs = new Junction[3];

	// utak szomszedokhoz, 0 ha nincs, player(1,2,3,4)
	public int[] road = new int[3];

	public Junction nextJunc;

	public Junction(int px, int py, int pNo) {
		super(px,py);
		haz=null;
		no=pNo;
		nTiles=0;
		nJuncs=0;
		nextJunc = null;
	}
	public void addTile(Tile pTile) {
		tiles[nTiles++] = pTile;
	}
	public void addJunc(Junction pJuncs) {
		boolean alreadyIn=false;
		for(int k=0;k<nJuncs;k++) if(juncs[k]==pJuncs) alreadyIn=true;
		if(!alreadyIn) juncs[nJuncs++] = pJuncs;
	}
	public void addUt(Junction pJuncs, int pPlayer) {
		int utPos = -1;
		for(int k=0;k<nJuncs;k++) if(juncs[k]==pJuncs) utPos=k;
		if(utPos!=-1) road[utPos]=pPlayer; else U.o("addUt error");
	}
	public int getUt(Junction pJuncs) {
		int utPos = -1;
		for(int k=0;k<nJuncs;k++) if(juncs[k]==pJuncs) utPos=k;
		if(utPos!=-1) return road[utPos]; else return 0;
	}
	public void removeUt(Junction pJuncs) {
		int utPos = -1;
		for(int k=0;k<nJuncs;k++) if(juncs[k]==pJuncs) utPos=k;
		if(utPos!=-1) road[utPos]=0; else U.o("removeUt error");
	}
	public boolean vanszomszed() {
		boolean vansz=false;
		for(int k=0;k<nJuncs;k++) 
			if(juncs[k].haz.type>0)
				vansz=true;
		return vansz;
	}
	public boolean vanideut(int pPlayer) {
		boolean vanut=false;
		for(int k=0;k<nJuncs;k++) 
			if(road[k]==pPlayer)
				vanut=true;
		return vanut;
	}
}
