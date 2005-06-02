package de.fh_zwickau.pti.whzintravoip.thin_client;

import javax.swing.*;

import com.borland.jbcl.layout.*;
import java.awt.*;

/**
 * <p>‹berschrift: </p>
 *
 * <p>Beschreibung: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann ys@fh-zwickau.de
 * @version 0.0.1
 */
public class Output extends JFrame{

    public Output(ThinClientGUI gui) {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        Output output = new Output();
    }

    public void stdOutput(String std) {
//        jTextArea.append("Info: " + std + "\n");
        jTextArea1.append("Info: " + std + "\n");
    }

    public void errOutput(String err) {
//        jTextArea.append("Error: " + err + "\n");
        jTextArea1.append("Error: " + err + "\n");
    }


    private void jbInit() throws Exception {
        jPanel1 = (JPanel) getContentPane();
        jPanel1.setLayout(xYLayout1);
        jTextArea1.setText("");
        jButtonCloseWindow.setText("Fenster schlieﬂen");
        jPanel1.add(jScrollPane1, new XYConstraints(15, 15, 500, 500));
        jPanel1.add(jButtonCloseWindow, new XYConstraints(15, 525, 500, 25));
        jScrollPane1.getViewport().add(jTextArea1);
        jPanel1.setLayout(xYLayout1);
    }

    JPanel jPanel1;
    XYLayout xYLayout1 = new XYLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea jTextArea1 = new JTextArea();
    JButton jButtonCloseWindow = new JButton();
}
