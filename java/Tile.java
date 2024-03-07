import javax.swing.*;

public class Tile extends Node {

	// T-enger, K-ikoto, N-ormal, S-ivatag

	public char type;
	// 0-semmi 1-kishaz 2-varos

	public int pontertek;

	public boolean hasRablo;

	public ImageIcon icon;
	
	// kikoto eseten resource ImageIcon
	public ImageIcon resIcon;

	// szomszed Normal mezok (max 6)
	public int nTiles;
	public Tile[] tiles = new Tile[6]; 

	// szomszed Junction-ok, 6 db
	public Junction[] juncs = new Junction[6];
	public int nJuncs; // Kikotonel csak 2 vagy 3;

	// kikoto Ora, 1,3,5,7,9,11 oranal lehet kikoto Junction, ketto/kikoto
	public boolean kO[] = new boolean[12];

	public Tile nextTile; // next Normal Tile
	public Tile nextAllTile; // next any Tile

	public Tile(int px, int py) {
		super(px,py);
		resource=' '; 
		pontertek=0; 
		hasRablo=false;
		nTiles=0;
		nJuncs=0;
		nextTile=null;
		for(int i=0;i<11;i++) kO[i]=false;
	}
};
