package de.fh_zwickau.pti.whzintravoip.thin_client;

import javax.swing.*;

import com.borland.jbcl.layout.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

/**
 * <p>Überschrift: </p>
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

    ThinClientGUI userGUI = null;

    public Output(ThinClientGUI gui) {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.userGUI = gui;
    }

    public static void main(String[] args) {
//        Output output = new Output();
    }

    public void stdOutput(String std) {
//        jTextArea.append("Info: " + std + "\n");
        jTextArea1.append("-------------------------------------\nInfo: "
                          + std
                          + "\n");
    }

    public void errOutput(String err) {
//        jTextArea.append("Error: " + err + "\n");
        jTextArea1.append("=====================================\nError: "
                          + err
                          + "\n");
    }


    private void jbInit() throws Exception {
        jPanel1 = (JPanel) getContentPane();
        jPanel1.setLayout(gridBagLayout1);
        jTextArea1.setText("");
        jButtonCloseWindow.setText("Fenster schließen");
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

    public void jButtonCloseWindow_actionPerformed(ActionEvent e) {
//        setVisible(false);
        userGUI.toggleOutputWindow();
    }

    public void this_windowClosed(WindowEvent e) {
//        setVisible(false);
        userGUI.setToggleWindowButtonName("Ausgabefenster öffnen");
    }

    public void this_windowClosing(WindowEvent e) {
        userGUI.setToggleWindowButtonName("Ausgabefenster öffnen");
    }
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
