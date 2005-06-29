package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Überschrift: WHZIntraVoIP, ThinClientGUI</p>
 *
 * <p>Beschreibung: Oberfläche für den Client</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann ys@fh-zwickau.de
 * @version 0.0.1
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.borland.jbcl.layout.*;

public class ThinClientGUI extends JFrame{

    private int m_iWindowSizeX     = 524;
    private int m_iWindowSizeY     = 327;
    private String m_sVersion      = "V0.5";

    private ThinClient m_ThinClient = null;

    private static final byte LOGIN    = 1;
    private static final byte PICKUP   = 2;
    private static final byte INCOMING = 3;
    private static final byte MAKECALL = 4;
    private static final byte CALLING  = 5;
    private static final byte TALKING  = 6;

//    private enum Status {
//        LOGIN, PICKUP, INCOMING, MAKECALL, CALLING, TALKING;
//    }

//    private Status status2 = Status.LOGIN;

    public ThinClientGUI(ThinClient client) {
        this.m_ThinClient = client;
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // einige Settings zum Programmfenster
        this.setSize(m_iWindowSizeX, m_iWindowSizeY);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int screenX = dimension.width;
        int screenY = dimension.height;
        this.setLocation((screenX - m_iWindowSizeX), 0);
        jLabelForVersion.setText(m_sVersion);

        // eigene IP ermitteln und in Textfeld eintragen
        jTextFieldMyIP.setText(m_ThinClient.extractOwnIP());
    }

    /**
     * Button zum Registrieren am Server
     *
     * @param e ActionEvent
     */
    public void jButtonRegister_actionPerformed(ActionEvent e) {
        m_ThinClient.signOn();
    }

    /**
     * Button zum updaten der Userliste
     *
     * @param e ActionEvent
     */
    public void jButtonUpdateUsers_actionPerformed(ActionEvent e) {
        m_ThinClient.whoIsOnAtServer();
    }

    /**
     * Startet den Receiver-Stack und setzt den eigenen Status auf PICKUP
     *
     * @param e ActionEvent
     */
    public void jButtonStartReceiver_actionPerformed(ActionEvent e) {
        m_ThinClient.startReceiver();
    }

    /**
     * Wenn das Fenster geschlossen wird, wird der SIPStack gelöscht und dann
     * das Programm beendet
     *
     * @param e WindowEvent
     */
    public void this_windowClosing(WindowEvent e) {
        m_ThinClient.exitClient();
    }

    /**
     * Button zum Ein- bzw. Ausblenden des Textfensters wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonToggleOutputWindow_actionPerformed(ActionEvent e) {
        m_ThinClient.toggleOutputWindow();
    }

    /**
     * This method returns the Container of the GUI output window. It is used
     * by the UserTreeGenerator to put the JPanel with the user tree in.
     *
     * @return Container - the ContentPane of the GUI window
     */
    public Container getGUIContentPane(){
        return this.getContentPane();
    }

    /**
     * Button zum Anrufen wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonHandleCall_actionPerformed(ActionEvent e) {
        m_ThinClient.handleCall();
    }

    /**
     * The pulldown menu "Info" was choosen...
     *
     * @param e ActionEvent
     */
    public void jMenuInfo_actionPerformed(ActionEvent e) {
        String message = "WHZIntraVoIP " + m_sVersion + "\n"
                         + "\n"
                         + "Softwareprojekt der FH Zwickau\n"
                         + "Sommersemester 2005\n"
                         + "\n"
                         + "Torsten Schmidt <torssch@fh-zwickau.de> - Serveranbindung\n"
                         + "Holger Seidel <hs@fh-zwickau.de> - Audiostreaming\n"
                         + "Yves Schumann <ys@fh-zwickau.de> - Sessionauf- und -abbau, GUI";
        String title = "Über WHZIntraVoIP";
        JOptionPane.showConfirmDialog(this, message, title, JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    public void jMenuRegisterAtServer_actionPerformed(ActionEvent e) {
        m_ThinClient.signOn();
    }
    public void jMenuWhoIsOn_actionPerformed(ActionEvent e) {
        m_ThinClient.whoIsOnAtServer();
    }
    public void jMenuExit_actionPerformed(ActionEvent e) {
        m_ThinClient.exitClient();
    }
    public void jTestButton_actionPerformed(ActionEvent e) {
        m_ThinClient.testButton(jTextFieldMyIP.getText());
    }
    public JTextField getTextFieldMyIP(){
        return jTextFieldMyIP;
    }
    public JTextArea getTextAreaUserInfo(){
        return jUserInfoField;
    }
    public JButton getButtonStartReceiver(){
        return jButtonStartReceiver;
    }
    public JButton getButtonToggleOutputWindow(){
        return jButtonToggleOutputWindow;
    }
    public JButton getButtonHandleCall(){
        return jButtonHandleCall;
    }
    public JButton getButtonTest(){
        // wurde benötigt um den Test-Button zu übergeben
        return null;
    }
    public JButton getButtonRegister(){
        return jButtonForRegistering;
    }
    public JButton getButtonUpdate(){
        return jButtonForUpdate;
    }
    public JMenuItem getMenuRegister(){
        return jMenuRegisterAtServer;
    }
    public JMenuItem getMenuUpdate(){
        return jMenuWhoIsOn;
    }

    /**
     * Init-Methode um das Hauptfenster zu etablieren
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {
        this.getContentPane().setLayout(gridBagLayout1);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setJMenuBar(jMenuBar1);
        jTextFieldMyIP.setMinimumSize(new Dimension(50, 21));
        jTextFieldMyIP.setText("127.0.0.1");
        jLabelForIP.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabelForIP.setText("My IP");
        this.addWindowListener(new ThinClientGUI_this_windowAdapter(this));
        jButtonToggleOutputWindow.setText("Ausgabefenster öffnen");
        jButtonToggleOutputWindow.addActionListener(new
                ThinClientGUI_jButtonToggleOutputWindow_actionAdapter(this));
        jButtonHandleCall.setEnabled(false);
        jButtonHandleCall.setText("Anrufen");
        jButtonHandleCall.addActionListener(new
                ThinClientGUI_jButtonMakeCall_actionAdapter(this));
        jButtonStartReceiver.setText("Start Receiver");
        jButtonStartReceiver.addActionListener(new
                ThinClientGUI_jButtonStartReceiver_actionAdapter(this));
        jUserInfoField.setBorder(BorderFactory.createLoweredBevelBorder());
        jUserInfoField.setEditable(false);
        jUserInfoField.setText("");
        jButtonForRegistering.setEnabled(false);
        jButtonForRegistering.setToolTipText("");
        jButtonForRegistering.setText("Register");
        jButtonForRegistering.addActionListener(new
                ThinClientGUI_jButtonForTests_actionAdapter(this));
        jMenu1.setText("Programm");
        jMenu2.setText("?");
        jMenuExit.setText("Ende");
        jMenuExit.addActionListener(new ThinClientGUI_jMenuExit_actionAdapter(this));
        jMenuInfo.setText("Info");
        jMenuInfo.addActionListener(new ThinClientGUI_jMenuInfo_actionAdapter(this));
        jButtonForUpdate.setEnabled(false);
        jButtonForUpdate.setText("Update");
        jButtonForUpdate.addActionListener(new
                                            ThinClientGUI_jButtonForUpdate_actionAdapter(this));

        // Information panel for user infos
        jInfoPanel.setLayout(xYLayout2);

        /////////
        // Pulldown menu "Register"
        jMenuRegisterAtServer.setText("Am Server anmelden");
        jMenuRegisterAtServer.setEnabled(false);
        jMenuRegisterAtServer.addActionListener(new
                                                ThinClientGUI_jMenuRegisterAtServer_actionAdapter(this));
        // Pulldown menu "Update"
        jMenuWhoIsOn.setText("Wer ist online?");
        jMenuWhoIsOn.setEnabled(false);
        jMenuWhoIsOn.addActionListener(new
                                       ThinClientGUI_jMenuWhoIsOn_actionAdapter(this));
        jLabelForVersion.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabelForVersion.setText("");
        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenu2);
        jMenu1.add(jMenuRegisterAtServer);
        jMenu1.add(jMenuWhoIsOn);
        jMenu1.add(jMenuExit);
        jMenu2.add(jMenuInfo);
        jInfoPanel.add(jUserInfoField, new XYConstraints(2, 2, 200, 150));
        this.getContentPane().add(jButtonStartReceiver,
                                  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 2, 2));
        this.getContentPane().add(jLabelForIP,
                                  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 2, 2));
        this.getContentPane().add(jTextFieldMyIP,
                                  new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 2, 2));
        this.getContentPane().add(jInfoPanel,
                                  new GridBagConstraints(2, 1, 2, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 2, 2));
        this.getContentPane().add(jButtonHandleCall,
                                  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 2, 2));
        this.getContentPane().add(jButtonForRegistering,
                                  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 2, 2));
        this.getContentPane().add(jButtonForUpdate,
                                  new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 2, 2));
        this.getContentPane().add(jLabelForVersion,
                                  new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 2, 2));
        this.getContentPane().add(jButtonToggleOutputWindow,
                                  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 2, 2));
    }

    JTextField jTextFieldMyIP = new JTextField();
    JLabel jLabelForIP = new JLabel();
    TitledBorder titledBorder1 = new TitledBorder("");
    TitledBorder titledBorder2 = new TitledBorder("");
    TitledBorder titledBorder3 = new TitledBorder("");
    JButton jButtonToggleOutputWindow = new JButton();
    JButton jButtonHandleCall = new JButton();
    JButton jButtonStartReceiver = new JButton();
    JTextArea jUserInfoField = new JTextArea();
    JButton jButtonForRegistering = new JButton();
    JButton jButtonDeleteAllEntries = new JButton();
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenu1 = new JMenu();
    JMenu jMenu2 = new JMenu();
    JMenuItem jMenuExit = new JMenuItem();
    JMenuItem jMenuInfo = new JMenuItem();
    JButton jButtonForUpdate = new JButton();
    JPanel jInfoPanel = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    FlowLayout flowLayout1 = new FlowLayout();
    XYLayout xYLayout1 = new XYLayout();
    XYLayout xYLayout2 = new XYLayout();
    JMenuItem jMenuRegisterAtServer = new JMenuItem();
    JMenuItem jMenuWhoIsOn = new JMenuItem();
    JLabel jLabelForVersion = new JLabel();
}


class ThinClientGUI_jMenuRegisterAtServer_actionAdapter implements
        ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jMenuRegisterAtServer_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuRegisterAtServer_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonForUpdate_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonForUpdate_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonUpdateUsers_actionPerformed(e);
    }
}


class ThinClientGUI_jMenuExit_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jMenuExit_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuExit_actionPerformed(e);
    }
}


class ThinClientGUI_jMenuInfo_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jMenuInfo_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuInfo_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonForTests_actionAdapter implements
        ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonForTests_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.jButtonRegister_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonStartReceiver_actionAdapter implements
        ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonStartReceiver_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonStartReceiver_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonMakeCall_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonMakeCall_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonHandleCall_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonToggleOutputWindow_actionAdapter implements
        ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonToggleOutputWindow_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonToggleOutputWindow_actionPerformed(e);
    }
}


class ThinClientGUI_this_windowAdapter extends WindowAdapter {
    private ThinClientGUI adaptee;
    ThinClientGUI_this_windowAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void windowClosing(WindowEvent e) {
        adaptee.this_windowClosing(e);
    }
}


class ThinClientGUI_jMenuWhoIsOn_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jMenuWhoIsOn_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuWhoIsOn_actionPerformed(e);
    }
}
