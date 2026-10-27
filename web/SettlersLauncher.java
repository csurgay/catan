import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SettlersLauncher {
    public static void main(String[] args) throws Exception {
        Settlers applet = new Settlers();   // the applet class in the jar (default package)
        applet.init();                      // builds the GUI itself via invokeAndWait - call from main thread
        applet.start();
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("Settlers of Catan - P.Csurgay 2004");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(applet);
            frame.setSize(1024, 768);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
