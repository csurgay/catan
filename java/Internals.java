import java.awt.*;
import javax.swing.*;

public class Internals extends JComponent{
	
	S parent;
	
	public Internals(S pParent) {
		parent=pParent;
		setBounds(10,10,300,150);
//		setBorder(new javax.swing.border.BevelBorder(1));
	}

	public void paintComponent(Graphics gg) {
		Graphics2D g = (Graphics2D)gg.create();
		g.setStroke(new BasicStroke(1.5f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		
		if(S.internals) {
			char[] c = new char[1];
			new Integer(S.noRounds).toString().getChars(0,1,c,0);
			g.drawChars(c,0,1,5,12);
		}
		g.dispose();
	}
}