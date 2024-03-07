import java.awt.*;
import javax.swing.*;

public class U {

	static Color[] playerColor=new Color[] {
		Color.black,Color.red,Color.blue,Color.white,new Color(255,150,10),
		new Color(0,190,0), new Color(153,92,0) };
	static Color pontertekColor=new Color(255,204,102); 
	static Color kikotoColor=new Color(210,210,210);
//	static Color hatterColor=new Color(200,200,255);
//	static Color hatterColor=new Color(180,180,255);
	static Color hatterColor=null;
//	static Color hatterColor=new Color(50,100,50);
//	static Color playerHatterColor=new Color(170,170,255);
	static Color kockaColor = new Color(255,215,148);
	static Color kockaInactiveColor = new Color(255,235,208);

	public static void o(String s) { System.out.println(s);}
	public static void o(int i) { System.out.println(new Integer(i).toString());}
	public static void o(int i,int j) { System.out.println(new Integer(i).toString()+' '+new Integer(j).toString());}
	public static void o(char i,char j) { System.out.println(new Character(i).toString()+' '+new Character(j).toString());}

	public static void bang(String s) {
		JOptionPane.showMessageDialog(null,s,
		"Warning",JOptionPane.WARNING_MESSAGE,null);
	}
}
