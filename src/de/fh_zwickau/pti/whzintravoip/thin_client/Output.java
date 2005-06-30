package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Title: WHZIntraVoIP</p>
 *
 * <p>Description: This class opens a output window for textmessages</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann <ys@fh-zwickau.de>
 * @version 0.1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Output extends JFrame {

    /**
     * just to store the WHZIntraVoIP main class
     */
    private ThinClient m_ThinClient = null;

    /**
     * This class creates a output window for all messages of the other classes.
     * The window will appear on the top left of the screen. After opening it,
     * it will cache all messages, even if you close the window and re-open it
     * again.
     * @param client ThinClient - the main class for interaction
     */
    public Output(ThinClient client) {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.m_ThinClient = client;
    }

    /**
     * The main method is not used, it does nothing
     * @param args String[]
     */
    public static void main(String[] args) {
    }

    /**
     * An info string to display in the output window. The method will insert
     * a horizontal line of dashes and on the next line the Text "Info: "
     * followed by the given string.
     * @param std String - the string to write in the window
     */
    public void stdOutput(String std) {
        jTextArea1.append("-------------------------------------\n"
                          + "Info: " + std + "\n");
    }

    /**
     * An error string to display in the output window. The method will insert
     * a horizontal line of dashes and on the next line the Text "Error: "
     * followed by the given string.
     * @param err String - the string to write in the window
     */
    public void errOutput(String err) {
        jTextArea1.append("=====================================\n"
                          + "Error: " + err + "\n");
    }

    /**
     * Calls on the ThinClient toggleOutputWindow()
     * @param e ActionEvent
     */
    public void jButtonCloseWindow_actionPerformed(ActionEvent e) {
//        setVisible(false);
        m_ThinClient.toggleOutputWindow();
    }

    /**
     * Calls on the ThinClient setToggleWindowButtonName("Ausgabefenster öffnen")
     * @param e WindowEvent
     */
    public void this_windowClosed(WindowEvent e) {
        m_ThinClient.setToggleWindowButtonName("Ausgabefenster öffnen");
    }

    /**
     * Calls on the ThinClient setToggleWindowButtonName("Ausgabefenster öffnen")
     * @param e WindowEvent
     */
    public void this_windowClosing(WindowEvent e) {
        m_ThinClient.setToggleWindowButtonName("Ausgabefenster öffnen");
    }

    /**
     * Initializes the window, the layout, the button and so on...
     * @throws Exception
     */
    private void jbInit() throws Exception {
        jPanel1 = (JPanel) getContentPane();
        jPanel1.setLayout(gridBagLayout1);
        jTextArea1.setText("");
        jButtonCloseWindow.setText("Fenster schließen");
        jButtonCloseWindow.setToolTipText("Messages werden weiterhin geschrieben, "
                                          + "auch wenn das Fenster wieder geschlossen wird");
        jButtonCloseWindow.addActionListener(new
                                             Output_jButtonCloseWindow_actionAdapter(this));
        this.addWindowListener(new Output_this_windowAdapter(this));
        jScrollPane1.getViewport().add(jTextArea1);
        jPanel1.add(jScrollPane1, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(15, 15, 0, 15), 500, 500));
        jPanel1.add(jButtonCloseWindow,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(15, 15, 15, 15), 380, 0));
    }

    JPanel jPanel1;
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea jTextArea1 = new JTextArea();
    JButton jButtonCloseWindow = new JButton();
    GridBagLayout gridBagLayout1 = new GridBagLayout();

}


class Output_jButtonCloseWindow_actionAdapter implements ActionListener {
    private Output adaptee;
    Output_jButtonCloseWindow_actionAdapter(Output adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonCloseWindow_actionPerformed(e);
    }
}


class Output_this_windowAdapter extends WindowAdapter {
    private Output adaptee;
    Output_this_windowAdapter(Output adaptee) {
        this.adaptee = adaptee;
    }

    public void windowClosed(WindowEvent e) {
        adaptee.this_windowClosed(e);
    }
}
