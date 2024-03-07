

abstract class Node {

	public int x,y;

	// type=KikotoTile: 3-3:1, f-a, t-egla, b-uza, j-uh, k-o
	// type=NormalTile: e-rdo, a-gyag, g-abona, l-egelo, h-egy
	// type=Junction: ' ' vagy kikoto(3-3:1, f-a, t-egla, b-uza, j-uh, k-o)
	public char resource;

	// 0-semmi 1-kishaz 2-varos
	public int occupy;

	// next Node (nextT ile for Tile, next Junction for Junction)
	public Node nextNode;
	
	// distance*distance from mousePointer(x,y)
	public int distance;

	public Node(int px, int py) {
		x=px; y=py; 
		resource=' ';
		nextNode=null; 
	}
};
